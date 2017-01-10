package com.nix.camel.router;

import com.nix.camel.config.CamelBeans;
import com.nix.service.FakeRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import javax.xml.transform.Source;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {
        JsonToXmlRouteTest.TestConfig.class,
        DataSourceAutoConfiguration.class,
        CamelAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
})
@ActiveProfiles("test")
@Slf4j
public class JsonToXmlRouteTest {

    private static final String ROUTE_ID = "json-to-xml";
    private static final String COMPARED_FILE_NAME = "user_001.xml";

    @EndpointInject(uri = "mock:jpa")
    protected MockEndpoint mockJpa;

    @EndpointInject(uri = "mock:log")
    protected MockEndpoint mockLog;

    @Value("${json.to.xml.in}")
    private String pathInbox;

    @Value("${json.to.xml.out}")
    private String pathOutbox;

    @Value("${json.to.xml.out.expected}")
    private String pathOutboxExpected;

    @Autowired
    private CamelContext camelContext;

    @Before
    public void setUp() throws Exception {
        FileUtils.cleanDirectory(FileUtils.getFile(pathOutbox));
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.cleanDirectory(FileUtils.getFile(pathOutbox));
    }

    @DirtiesContext
    @Test
    public void testJsonToXmlRoute() throws Exception {

        File inDir = FileUtils.getFile(pathInbox);
        File outDir = FileUtils.getFile(pathOutbox);
        int expectedFilesQuantity = 3;
        int expectedMessagesQuantity = 3;
        int timeoutSeconds = 5;

        camelContext.start();
        camelContext.startRoute(ROUTE_ID);

        mockJpa.expectedMessageCount(expectedFilesQuantity);

        NotifyBuilder notify = new NotifyBuilder(camelContext)
                .whenDone(expectedMessagesQuantity).create();

        assertTrue("three messages should be created within " + timeoutSeconds + " seconds",
                notify.matches(timeoutSeconds, TimeUnit.SECONDS));
        assertEquals("input files should not be removed",
                inDir.listFiles().length, expectedFilesQuantity);
        assertEquals("three xml files should be created",
                outDir.listFiles().length, expectedFilesQuantity);

        MockEndpoint.assertIsSatisfied(camelContext, timeoutSeconds, TimeUnit.SECONDS);

        Source expected = Input.fromFile(pathOutbox + COMPARED_FILE_NAME).build();
        Source result = Input.fromFile(pathOutboxExpected + COMPARED_FILE_NAME).build();

        Diff myDiff = DiffBuilder
                .compare(expected)
                .withTest(result)
                .checkForSimilar()
                .ignoreWhitespace()
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
                .build();

        Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());

    }

    @DirtiesContext
    @Test
    public void testJsonToXmlRoute_RoleEnrichException() throws Exception {

        File inDir = FileUtils.getFile(pathInbox);
        File outDir = FileUtils.getFile(pathOutbox);
        int expectedInputFilesQuantity = 3;
        int expectedOutputFilesQuantity = 0;
        int expectedMessagesQuantity = 3;
        int timeoutSeconds = 5;

        RouteDefinition route = camelContext.getRouteDefinition(ROUTE_ID);
        route.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("log:*")
                        .to("mock://log");

                intercept().id("role-enricher")
                        .throwException(new Exception());
            }
        });

        mockJpa.expectedMessageCount(expectedOutputFilesQuantity);
        mockLog.expectedMessageCount(expectedInputFilesQuantity);

        camelContext.start();
        camelContext.startRoute(ROUTE_ID);

        NotifyBuilder notify = new NotifyBuilder(camelContext)
                .whenDone(expectedMessagesQuantity).create();

        assertTrue("three messages should be created within " + timeoutSeconds + " seconds",
                notify.matches(timeoutSeconds, TimeUnit.SECONDS));
        assertEquals("input files should not be removed",
                inDir.listFiles().length, expectedInputFilesQuantity);
        assertEquals("zero xml files should be created in output dir",
                outDir.listFiles().length, expectedOutputFilesQuantity);

        MockEndpoint.assertIsSatisfied(camelContext, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Configuration
    @ComponentScan(basePackageClasses = {
            FakeRoleService.class,
            CamelBeans.class,
    })
    public static class TestConfig {

        @Bean
        public JsonToXmlRoute jsonToXmlRoute() {
            return new JsonToXmlRoute();
        }

        @Bean
        public CamelContextConfiguration contextConfiguration() {
            return new CamelContextConfiguration() {

                @Override
                public void beforeApplicationStart(CamelContext camelContext) {
                    camelContext.setAutoStartup(false);

                    RouteDefinition route = camelContext.getRouteDefinition(ROUTE_ID);
                    try {
                        route.adviceWith(camelContext, new AdviceWithRouteBuilder() {
                            @Override
                            public void configure() throws Exception {
                                interceptSendToEndpoint("jpa:*")
                                        .skipSendToOriginalEndpoint()
                                        .to("mock://jpa");
                            }
                        });
                    } catch (Exception e) {
                        log.error("Can not mock jpa route. {}", e);
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void afterApplicationStart(CamelContext camelContext) {
                }
            };
        }
    }
}