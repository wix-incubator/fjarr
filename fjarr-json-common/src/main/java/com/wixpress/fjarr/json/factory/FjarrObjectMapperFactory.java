package com.wixpress.fjarr.json.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.fjarr.json.FjarrJacksonModule;

/**
 * @author: ittaiz
 * @since: 6/6/13
 */
public class FjarrObjectMapperFactory {
    public static ObjectMapper anObjectMapperWithFjarrModule() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());
        return mapper;
    }
}
