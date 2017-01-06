package com.nix.camel.router;

import com.nix.camel.config.CamelBeans;
import com.nix.service.FakeRoleServiceImpl;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.naming.Context;
import java.util.Properties;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JsonToXmlRouterTest_3.class, CamelBeans.class})
@ActiveProfiles("test")
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = FakeRoleServiceImpl.class)
public class JsonToXmlRouterTest_3 extends CamelTestSupport {

    @EndpointInject(uri = "file:src/test/resources/data/json-to-xml/in/")
    protected ProducerTemplate animalSource;

    @EndpointInject(uri = "mock:dogEndpoint")
    protected MockEndpoint dogEndpoint;

    @Test
    public void contextLoads() {

        animalSource.sendBodyAndHeader("test","C","D");


    }
}