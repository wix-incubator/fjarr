package com.wixpress.fjarr.json.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.fjarr.json.JsonRpcClientProtocol;

/**
 * @author: ittaiz
 * @since: 5/29/13
 */
public class JsonRPCClientProtocolFactory {
    public static JsonRpcClientProtocol aDefaultJsonRpcClientProtocolFrom() {
        return aJsonRpcClientProtocolFrom(new ObjectMapper());
    }

    public static JsonRpcClientProtocol aJsonRpcClientProtocolFrom(ObjectMapper mapper) {
        return new JsonRpcClientProtocol(mapper);
    }
}
