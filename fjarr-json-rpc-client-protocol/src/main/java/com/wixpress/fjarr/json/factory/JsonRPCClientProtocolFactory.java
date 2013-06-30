package com.wixpress.fjarr.json.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.fjarr.json.JsonRpcClientProtocol;

import static com.wixpress.fjarr.json.factory.FjarrObjectMapperFactory.anObjectMapperWithFjarrModule;

/**
 * @author: ittaiz
 * @since: 5/29/13
 */
public class JsonRPCClientProtocolFactory {
    public static JsonRpcClientProtocol aDefaultJsonRpcClientProtocol() {
        return aJsonRpcClientProtocolFrom(anObjectMapperWithFjarrModule());
    }

    public static JsonRpcClientProtocol aJsonRpcClientProtocolFrom(ObjectMapper mapper) {
        return new JsonRpcClientProtocol(mapper);
    }
}
