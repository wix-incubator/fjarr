package com.wixpress.framework.rpc.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.wixpress.framework.rpc.server.NamedRpcParameters;
import com.wixpress.framework.rpc.server.PositionalRpcParameters;
import com.wixpress.framework.rpc.server.RpcRequest;
import com.wixpress.framework.rpc.server.exceptions.BadRequestException;
import com.wixpress.fjarr.reflection.parameters.AnnotationParameterNameDiscoverer;
import com.wixpress.fjarr.reflection.parameters.ParameterNameDiscoverer;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author AlexeyR
 * @since 6/15/11 10:29 PM
 */

public class JsonRpcProtocolParserTest
{

    private ObjectMapper mapper = new ObjectMapper();
    private ParameterNameDiscoverer d = new AnnotationParameterNameDiscoverer();
    JsonRpcProtocol protocol = new JsonRpcProtocol(mapper, d);


    @Test
    public void testPositionalParamsParseSuccess() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"1\"}";
        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).getMethodName(), is("subtract"));
        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(PositionalRpcParameters.class));
        assertThat(((IntNode) ((PositionalRpcParameters) request.getInvocations().get(0).getParameters()).getParameters()[0]).intValue(), is(42));
        assertThat(((IntNode) ((PositionalRpcParameters) request.getInvocations().get(0).getParameters()).getParameters()[1]).intValue(), is(23));
        assertThat(((JsonNode) request.getInvocations().get(0).getValueFromContext("id")).textValue(), is("1"));
    }

    @Test
    public void testNamedParamsParseSuccess() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"a\":42, \"b\" : 23}, \"id\": 1}";
        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).getMethodName(), is("subtract"));
        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(NamedRpcParameters.class));
        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("a")).intValue(), is(42));
        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("b")).intValue(), is(23));
        assertThat(((JsonNode) request.getInvocations().get(0).getValueFromContext("id")).intValue(), is(1));
    }

    @Test
    public void testNotificationParseSuccess() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"a\":42, \"b\" : 23}}";
        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).getMethodName(), is("subtract"));
        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(NamedRpcParameters.class));
        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("a")).intValue(), is(42));
        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("b")).intValue(), is(23));
        assertThat(request.getInvocations().get(0).getValueFromContext("id"), Matchers.<Object>nullValue());
        assertThat((Boolean) request.getInvocations().get(0).getValueFromContext("notification"), is(true));
    }


    @Test
    public void testBatchRequestParseSuccess() throws IOException, BadRequestException
    {
        String json = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23]}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 2}]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(3));
        assertThat(((JsonNode) request.getInvocations().get(0).getValueFromContext("id")).intValue(), is(1));
        assertThat(((JsonNode) request.getInvocations().get(2).getValueFromContext("id")).intValue(), is(2));
        assertThat((Boolean) request.getInvocations().get(1).getValueFromContext("notification"), is(true));
    }

    @Test
    public void testBatchRequest() throws IOException, BadRequestException
    {
        String json = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23]}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"2\"}]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(3));
        assertThat(((JsonNode) request.getInvocations().get(0).getValueFromContext("id")).intValue(), is(1));
        assertThat(((JsonNode) request.getInvocations().get(2).getValueFromContext("id")).textValue(), is("2"));
        assertThat((Boolean) request.getInvocations().get(1).getValueFromContext("notification"), is(true));
    }


    @Test(expected = BadRequestException.class)
    public void testBadJsonNodeInt() throws IOException, BadRequestException
    {
        String json = "1";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
    }

    @Test(expected = BadRequestException.class)
    public void testBadJsonNodeString() throws IOException, BadRequestException
    {
        String json = "\"aaa\"";
        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
    }

    @Test
    public void testEmptyArray() throws IOException, BadRequestException
    {
        String json = "[]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));

    }

    @Test
    @Ignore("move to ITs this test - fails in the test code")
    public void testInvalidJson() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"foobar, \"params\": \"bar\", \"baz]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));

    }

    @Test
    public void testInvalidRequest() throws IOException, BadRequestException
    {

        String json = "{\"jsonrpc\": \"2.0\", \"method\": 1, \"params\": \"bar\"}";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));
    }


    @Test
    public void testBatchWithInvalidItems() throws IOException, BadRequestException
    {
        String json = "[1,{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1},2,3]";

        RpcRequest request = protocol.parseRequestJson(mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(4));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));
        assertThat(request.getInvocations().get(2).isError(), is(true));
        assertThat(request.getInvocations().get(2).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));
        assertThat(request.getInvocations().get(3).isError(), is(true));
        assertThat(request.getInvocations().get(3).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));
        assertThat(request.getInvocations().get(1).getMethodName(), is("subtract"));
        assertThat(request.getInvocations().get(1).getParameters(), instanceOf(PositionalRpcParameters.class));
        assertThat(((IntNode) ((PositionalRpcParameters) request.getInvocations().get(1).getParameters()).getParameters()[0]).intValue(), is(42));
        assertThat(((IntNode) ((PositionalRpcParameters) request.getInvocations().get(1).getParameters()).getParameters()[1]).intValue(), is(23));
        assertThat(((JsonNode) request.getInvocations().get(1).getValueFromContext("id")).intValue(), is(1));

    }

}
