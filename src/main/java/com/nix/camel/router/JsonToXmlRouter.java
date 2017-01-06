package com.nix.camel.router;

import com.nix.service.FakeRoleService;
import lombok.Setter;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class JsonToXmlRouter extends RouteBuilder {

    private static final String ROUTE_ID = "json-to-xml";

    @Autowired @Setter
    private JSONParser jsonParser;
    @Autowired @Setter
    private FakeRoleService roleService;

    @Override
    public void configure() throws Exception {
        from("file:{{json.to.xml.from}}?noop=true&delay=2000").routeId(ROUTE_ID)
                .process(ex -> {
                    JSONObject jsonObject =
                            (JSONObject) jsonParser.parse(ex.getIn().getBody(String.class));

                    jsonObject.computeIfPresent("role",
                            (s, o) -> {
                                String roleName = (String) o;
                                long roleId = roleService.findIdByName(roleName);
                                JSONObject role = new JSONObject();
                                role.put("id", roleId);
                                role.put("name", roleName);
                                return role;
                            });

                    ex.getIn().setBody(jsonObject.toString());
                })
                .unmarshal("xmlJsonDataFormat")
                .to("file:{{json.to.xml.to}}?fileName=${file:name.noext}.xml")
                .unmarshal().xstream()
//                .to("jpa:" + User.class.getCanonicalName());
                .to("jpa:com.nix.User");

//                .process(ex -> {
//                    System.out.println(ex.getIn().getBody());
//                });

    }

}
