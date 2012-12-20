package com.wixpress.framework.rpc.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wixpress.framework.rpc.server.PositionalRpcParameters;
import com.wixpress.framework.rpc.server.RpcInvocation;
import com.wixpress.framework.rpc.server.RpcRequest;
import com.wixpress.framework.rpc.server.exceptions.BadRequestException;
import com.wixpress.fjarr.reflection.parameters.AnnotationParameterNameDiscoverer;
import com.wixpress.fjarr.reflection.parameters.ParameterNameDiscoverer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author AlexeyR
 * @since 6/16/11 9:52 AM
 */

public class JsonRpcProtocolMarshalerTest
{

    private static ObjectMapper mapper = new ObjectMapper();
    private ParameterNameDiscoverer d = new AnnotationParameterNameDiscoverer();
    JsonRpcProtocol protocol = new JsonRpcProtocol(mapper, d);

    private interface substracter
    {
        int substract(int x, int y);

        void t();
    }
    @BeforeClass
    public static void init()
    {
        mapper.registerModule(new HoopoeRpcJacksonModule());
    }

    @Test
    public void testSuccessResponse() throws IOException, BadRequestException, NoSuchMethodException
    {

        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        request.getInvocations().get(0).setInvocationResult(19);
        request.getInvocations().get(0).setResolvedMethod(substracter.class.getMethod("substract", Integer.TYPE, Integer.TYPE));

        JsonNode r = protocol.createResponseObject(request);
        assertThat(r.isObject(), is(true));
        ObjectNode response = (ObjectNode) r;
        assertThat(response.get("result").intValue(), is(19));
        assertThat(response.get("id").intValue(), is(1));

    }

    @Test
    public void testBatchRequestWithNotification() throws IOException, BadRequestException, NoSuchMethodException
    {

        String json = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23]}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 2}]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        request.getInvocations().get(0).setInvocationResult(19);
        request.getInvocations().get(0).setResolvedMethod(substracter.class.getMethod("substract", Integer.TYPE, Integer.TYPE));
        request.getInvocations().get(1).setInvocationResult(19);
        request.getInvocations().get(1).setResolvedMethod(substracter.class.getMethod("substract", Integer.TYPE, Integer.TYPE));
        request.getInvocations().get(2).setInvocationResult(19);
        request.getInvocations().get(2).setResolvedMethod(substracter.class.getMethod("substract", Integer.TYPE, Integer.TYPE));

        JsonNode r = protocol.createResponseObject(request);
        assertThat(r.isArray(), is(true));

        assertThat(r.size(), is(2));
        ObjectNode response = (ObjectNode) r.get(0);
        assertThat(response.get("result").intValue(), is(19));
        assertThat(response.get("id").intValue(), is(1));

        response = (ObjectNode) r.get(1);
        assertThat(response.get("result").intValue(), is(19));
        assertThat(response.get("id").intValue(), is(2));

    }


    @Test
    public void testEmptyArray() throws IOException, BadRequestException
    {

        String json = "[]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        JsonNode r = protocol.createResponseObject(request);
        assertThat(r.isObject(), is(true));
        assertThat(r.get("id"), instanceOf(NullNode.class));
        assertThat(r.get("error").get("code").intValue(), is(-32600));
    }


    @Test
    public void testInvalidRequest() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": 1, \"params\": \"bar\"}";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        JsonNode r = protocol.createResponseObject(request);
        assertThat(r.isObject(), is(true));
        assertThat(r.get("id"), instanceOf(NullNode.class));
        assertThat(r.get("error").get("code").intValue(), is(-32600));
    }


    @Test
    public void testBatchWithInvalidItems() throws IOException, BadRequestException, NoSuchMethodException
    {

        String json = "[1,{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1},2,3]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        request.getInvocations().get(1).setInvocationResult(19);
        request.getInvocations().get(1).setResolvedMethod(substracter.class.getMethod("substract", Integer.TYPE, Integer.TYPE));
        JsonNode r = protocol.createResponseObject(request);

        assertThat(r.isArray(), is(true));

        assertThat(r.get(0).get("id"), instanceOf(NullNode.class));
        assertThat(r.get(0).get("error").get("code").intValue(), is(-32600));

        assertThat(r.get(2).get("id"), instanceOf(NullNode.class));
        assertThat(r.get(2).get("error").get("code").intValue(), is(-32600));

        assertThat(r.get(3).get("id"), instanceOf(NullNode.class));
        assertThat(r.get(3).get("error").get("code").intValue(), is(-32600));

        assertThat(r.get(1).get("id").intValue(), is(1));

    }


    @Test
    public void testMarshallingException() throws IOException, BadRequestException, NoSuchMethodException, ClassNotFoundException
    {

        String json = "[1,{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1},2,3]";
        RpcInvocation i = new RpcInvocation("ttt", new PositionalRpcParameters(new Object[]{1, 2}));
        i.setError(new NullPointerException("aaaaaa"));

        RpcRequest request = RpcRequest.of(i);

        String s = mapper.writeValueAsString(protocol.createResponseObject(request));
        JsonNode r = mapper.readTree(s);

        assertThat(r.get("id"), instanceOf(NullNode.class));
        assertThat(r.get("error").get("code").intValue(), is(-32603));
        ObjectNode data = (ObjectNode) r.get("error").get("data");
        String clazz = data.get("@class").textValue();
        assertThat(data.get("@class").textValue(), is("java.lang.NullPointerException"));
    }
}
