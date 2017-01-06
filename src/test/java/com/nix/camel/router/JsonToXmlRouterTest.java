package com.nix.camel.router;

import com.nix.SbootCamelApplication;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.ExcludeRoutes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {JsonToXmlRouterTest.class, SbootCamelApplication.class})
@ActiveProfiles("test")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:beforeItTestRun.sql")
@ExcludeRoutes(value = XmlToJsonRouter.class)
public class JsonToXmlRouterTest {

    @Value("${json.to.xml.from}")
    private String pathInbox;
    @Value("${json.to.xml.to}")
    private String pathOutbox;
    @Autowired
    private CamelContext camelContext;

    @Before
    public void setUp() throws Exception {

    }

    @DirtiesContext
    @Test
    public void contextLoads() throws Exception {

        System.out.println(pathInbox + pathOutbox);
        NotifyBuilder notify = new NotifyBuilder(camelContext).whenDone(3).create();
        assertTrue(notify.matches(10, TimeUnit.SECONDS));
    }

}