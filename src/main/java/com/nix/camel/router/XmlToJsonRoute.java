package com.nix.camel.router;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.builder.RouteBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class XmlToJsonRoute extends RouteBuilder {

    private static final String ROUTE_ID = "xml-to-json";

    @Autowired
    private JSONParser jsonParser;

    @Override
    public void configure() throws Exception {

        onException(Throwable.class)
                .handled(true)
                .to("log:" + ROUTE_ID + "?level=ERROR&showAll=true");

        from("file:{{xml.to.json.in}}?noop=true&delay=2000").routeId(ROUTE_ID)
                .marshal("xmlJsonDataFormat")
                .process(ex -> {
                    JSONObject jsonObject =
                            (JSONObject) jsonParser.parse(ex.getIn().getBody(String.class));
                    jsonObject.computeIfPresent("role",
                            (s, o) -> ((JSONObject) o).get("name"));
                    ex.getIn().setBody(jsonObject.toString(JSONStyle.NO_COMPRESS));
                })
                .to("file:{{xml.to.json.out}}?fileName=${file:name.noext}.json")
                .to("log:xml-to-json-logger");
    }


}
