package com.nix.camel.router;

import com.nix.service.FakeRoleServiceImpl;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;

import javax.naming.Context;
import java.util.Properties;

public class JsonToXmlRouterTest_2 extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        JsonToXmlRouter router = new JsonToXmlRouter();
        router.setRoleService(new FakeRoleServiceImpl());
        router.setJsonParser(new JSONParser(JSONParser.MODE_JSON_SIMPLE));
        return router;
    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        Properties extra = new Properties();
        extra.put("json.to.xml.from", "src/test/resources/data/json-to-xml/in/");
        extra.put("json.to.xml.to", "src/test/resources/data/json-to-xml/out/");
        return extra;
    }

    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext context = new JndiContext();
        return context;
    }

    @Test
    public void contextLoads() {
//        NotifyBuilder notify = new NotifyBuilder(camelContext).whenDone(1).create();
//        assertTrue(notify.matches(10, TimeUnit.SECONDS));

        template.sendBody("jms:topic:quote", "Camel rocks");

    }
}