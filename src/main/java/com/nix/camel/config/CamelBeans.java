package com.nix.camel.config;

import com.nix.model.Role;
import com.nix.model.User;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import net.minidev.json.parser.JSONParser;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelBeans {

    @Bean
    public XmlJsonDataFormat xmlJsonDataFormat() {
        XmlJsonDataFormat bean = new XmlJsonDataFormat();
        bean.setRootName("user");
        return bean;
    }

    @Bean
    public JSONParser jsonParser() {
        return new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    }

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
        xStream.alias("user", User.class);
        xStream.alias("role", Role.class);

        String dateFormat = "yyyy-MM-dd";
        DateConverter converter = new DateConverter(dateFormat, null);
        xStream.registerConverter(converter);

        return xStream;
    }
}
