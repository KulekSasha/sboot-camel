package com.nix.camel.router;

import com.nix.camel.config.CamelBeans;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.ExcludeRoutes;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {JsonToXmlRouter.class, CamelBeans.class})
@ComponentScan(basePackages = {"com.nix.service", "com.nix.model"})
@ActiveProfiles("test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:beforeItTestRun.sql")
@EnableAutoConfiguration
public class JsonToXmlRouterTest {

    @Value("${json.to.xml.from}")
    private String pathInbox;
    @Value("${json.to.xml.to}")
    private String pathOutbox;
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
    public void contextLoads() throws Exception {

        File outDir = FileUtils.getFile(pathOutbox);


        System.out.println(pathInbox + pathOutbox);
        NotifyBuilder notify = new NotifyBuilder(camelContext).whenDone(10).create();
        assertTrue(notify.matches(3, TimeUnit.SECONDS));
        assertEquals("a", outDir.listFiles().length, 3);
    }


}