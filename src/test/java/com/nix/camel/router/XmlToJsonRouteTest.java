package com.nix.camel.router;

import com.nix.camel.config.CamelBeans;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {
        XmlToJsonRouteTest.TestConfig.class,
        CamelAutoConfiguration.class,
})
@ActiveProfiles("test")
@Slf4j
public class XmlToJsonRouteTest {

    private static final String ROUTE_ID = "xml-to-json";

    private static final String COMPARED_FILE_NAME = "user_001.json";

    @Value("${xml.to.json.in}")
    private String pathInbox;

    @Value("${xml.to.json.out}")
    private String pathOutbox;

    @Value("${xml.to.json.out.expected}")
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
    public void testXmlToJsonRoute() throws Exception {

        File inDir = FileUtils.getFile(pathInbox);
        File outDir = FileUtils.getFile(pathOutbox);
        int expectedFilesQuantity = 3;
        int expectedMessagesQuantity = 3;
        int timeoutSeconds = 5;

        camelContext.start();
        camelContext.startRoute(ROUTE_ID);

        NotifyBuilder notify = new NotifyBuilder(camelContext)
                .whenDone(expectedMessagesQuantity).create();

        assertTrue("three messages should be created within " + timeoutSeconds + " seconds",
                notify.matches(timeoutSeconds, TimeUnit.SECONDS));
        assertEquals("input files should not be removed",
                inDir.listFiles().length, expectedFilesQuantity);
        assertEquals("three xml files should be created",
                outDir.listFiles().length, expectedFilesQuantity);

        MockEndpoint.assertIsSatisfied(camelContext, timeoutSeconds, TimeUnit.SECONDS);

        File expectedFile = FileUtils.getFile(pathOutboxExpected + COMPARED_FILE_NAME);
        File resultFile = FileUtils.getFile(pathOutbox + COMPARED_FILE_NAME);
        JSONAssert.assertEquals(FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8),
                FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8),
                JSONCompareMode.LENIENT);
    }


    @Configuration
    @ComponentScan(basePackageClasses = CamelBeans.class)
    public static class TestConfig {

        @Bean
        public XmlToJsonRoute xmlToJsonRoute() {
            return new XmlToJsonRoute();
        }

        @Bean
        public CamelContextConfiguration contextConfiguration() {
            return new CamelContextConfiguration() {

                @Override
                public void beforeApplicationStart(CamelContext camelContext) {
                    camelContext.setAutoStartup(false);
                }

                @Override
                public void afterApplicationStart(CamelContext camelContext) {
                }
            };
        }
    }
}