package com.wixpress.fjarr.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wixpress.fjarr.client.RpcClientProtocol;
import com.wixpress.fjarr.client.exceptions.InvalidRpcResponseException;
import com.wixpress.fjarr.client.exceptions.RpcInvocationException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author alexeyr
 * @since 6/30/11 5:40 PM
 */

public class JsonRpcClientProtocol implements RpcClientProtocol {
    private static final String JSON_RPC_VERSION = "2.0";
    public static final String CONTENT_TYPE = "application/json-rpc";
    public static final String ACCCEPT_HEADER = "application/json";


    ObjectMapper mapper;

    private AtomicLong messageId = new AtomicLong(0);

    public JsonRpcClientProtocol(ObjectMapper mapper) {

        this.mapper = mapper;
    }

    @Override
    public String writeRequest(String methodName, Object[] arguments) throws IOException {
        // reset the messageId if needed
        messageId.compareAndSet(Long.MAX_VALUE, 0l);

        // create an array node of params and manually map each param to a JsonNode in order to avoid weird type info property of Object[]
        ArrayNode params = mapper.createArrayNode();
        for (Object argument : arguments) {
            params.add(mapper.valueToTree(argument));
        }

        ObjectNode request = mapper.createObjectNode();//writer. createObjectNode();
        request.put("id", messageId.getAndIncrement());
        request.put("jsonrpc", JSON_RPC_VERSION);
        request.put("method", methodName);
        request.put("params", params);

        // post the json data;
        return mapper.writeValueAsString(request);
    }

    @Override
    public <T> T readResponse(Type returnType, String response) throws RpcInvocationException {
        try {
            // read the response
            JsonNode root = mapper.readTree(response);

            // bail on invalid response
            if (!root.isObject()) {
                throw new InvalidRpcResponseException(String.format("Invalid JSON-RPC response - expected a JSON object but got [%s]", response));
            }
            ObjectNode jsonObject = ObjectNode.class.cast(root);

            // detect errors
            if (jsonObject.has("error") && !jsonObject.get("error").isNull()) {
                handleExceptionFromServer(jsonObject);
            } else if (jsonObject.has("result")) // convert it to a return object
            {
                if (returnType.equals(void.class))
                    return null;
                else
                    return getReader().readValue(getReader().treeAsTokens(jsonObject.get("result")), mapper.getTypeFactory().constructType(returnType));//.getRawClass()

            }

            throw new InvalidRpcResponseException(String.format(
                    "Invalid JSON-RPC response - expected either 'result' or 'error' but got [%s]", jsonObject));
        } catch (IOException ex) {
            throw new InvalidRpcResponseException("Failed reading response", ex);
        }
    }


    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getAcceptType() {
        return ACCCEPT_HEADER;
    }

    /**
     * This is a separate method so that the stack trace will contain "handleExceptionFromServer" rather than _readResponse,
     * thus making it clear to the user that this exception originated from the server rather than from the client
     *
     * @param jsonObject
     */
    private void handleExceptionFromServer(ObjectNode jsonObject) {
        ObjectNode errorObject = ObjectNode.class.cast(jsonObject.get("error"));

        Exception e;
        try {
            e = getReader().readValue(getReader().treeAsTokens(errorObject.get("data")), Exception.class);
            if (e instanceof RuntimeException) {
                e.setStackTrace(Thread.currentThread().getStackTrace());
            }
        } catch (Exception mappingException) // either we can't deserialize the exception or we're not familiar with it
        {
            throw new RpcInvocationException(
                    "JSON-RPC Error " + errorObject.get("code") + ": " +
                            errorObject.get("message"), errorObject.get("code").intValue());
        }
        // this is here and not in the try block because otherwise it will be caught in the catch block
        throw new RpcInvocationException(e);
    }

    // delaying the instanciation of ObjectReader till the first actual usage, because ObjectMapper is configured after the
    // this
    private ObjectReader getReader() {
        return mapper.reader().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

}
