package com.nix.camel.router;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class XmlToJsonRouter extends RouteBuilder {

    private static final String PATH_FROM = "src/main/resources/data/xml-to-json/in/";
    private static final String PATH_TO = "src/main/resources/data/xml-to-json/out/";

    @Autowired
    private JSONParser jsonParser;

    @Override
    public void configure() throws Exception {

        from("file:" + PATH_FROM + "?noop=true&delay=2000").routeId("xml-to-json")
                .marshal("xmlJsonDataFormat")
                .process(ex -> {
                    JSONObject jsonObject =
                            (JSONObject) jsonParser.parse(ex.getIn().getBody(String.class));
                    jsonObject.computeIfPresent("role",
                            (s, o) -> ((JSONObject) o).get("name"));
                    ex.getIn().setBody(jsonObject.toString(JSONStyle.NO_COMPRESS));
                })
                .to("file:" + PATH_TO + "?fileName=${file:name.noext}.json")

                .to("log:xml-to-json-logger");
    }



}
