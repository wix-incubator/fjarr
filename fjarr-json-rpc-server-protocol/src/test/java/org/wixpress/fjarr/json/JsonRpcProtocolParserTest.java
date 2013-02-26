package org.wixpress.fjarr.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.wixpress.fjarr.server.ObjectRpcParameters;
import org.wixpress.fjarr.server.ParsedRpcRequest;
import org.wixpress.fjarr.server.PositionalRpcParameters;
import org.wixpress.fjarr.server.RpcRequest;
import org.wixpress.fjarr.server.exceptions.BadRequestException;
import org.wixpress.fjarr.util.ReflectionUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author AlexeyR
 * @since 6/15/11 10:29 PM
 */

public class JsonRpcProtocolParserTest
{

    private ObjectMapper mapper = new ObjectMapper();
    JsonRpcProtocol protocol = new JsonRpcProtocol(mapper);


    @Test
    public void testPositionalParamsParseSuccess() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"1\"}";
        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
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
        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).getMethodName(), is("subtract"));
//        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(NamedRpcParameters.class));
//        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("a")).intValue(), is(42));
//        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("b")).intValue(), is(23));

        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(ObjectRpcParameters.class));
        ObjectNode on = (ObjectNode) ((ObjectRpcParameters) request.getInvocations().get(0).getParameters()).getParameters();
        assertThat(on.get("a").intValue(), is(42));
        assertThat(on.get("b").intValue(), is(23));
        assertThat(((JsonNode) request.getInvocations().get(0).getValueFromContext("id")).intValue(), is(1));


    }

    @Test
    public void testNotificationParseSuccess() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"a\":42, \"b\" : 23}}";
        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).getMethodName(), is("subtract"));
//        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(NamedRpcParameters.class));
//        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("a")).intValue(), is(42));
//        assertThat(((IntNode) ((NamedRpcParameters) request.getInvocations().get(0).getParameters()).getParameters().get("b")).intValue(), is(23));
        assertThat(request.getInvocations().get(0).getParameters(), instanceOf(ObjectRpcParameters.class));
        ObjectNode on = (ObjectNode) ((ObjectRpcParameters) request.getInvocations().get(0).getParameters()).getParameters();
        assertThat(on.get("a").intValue(), is(42));
        assertThat(on.get("b").intValue(), is(23));
        assertThat(request.getInvocations().get(0).getValueFromContext("id"), nullValue());
        assertThat((Boolean) request.getInvocations().get(0).getValueFromContext("notification"), is(true));
    }


    @Test
    public void testBatchRequestParseSuccess() throws IOException, BadRequestException
    {
        String json = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23]}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 2}]";

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
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

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(3));
        assertThat(((JsonNode) request.getInvocations().get(0).getValueFromContext("id")).intValue(), is(1));
        assertThat(((JsonNode) request.getInvocations().get(2).getValueFromContext("id")).textValue(), is("2"));
        assertThat((Boolean) request.getInvocations().get(1).getValueFromContext("notification"), is(true));
    }


    @Test(expected = BadRequestException.class)
    public void testBadJsonNodeInt() throws IOException, BadRequestException
    {
        String json = "1";

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
    }

    @Test(expected = BadRequestException.class)
    public void testBadJsonNodeString() throws IOException, BadRequestException
    {
        String json = "\"aaa\"";
        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
    }

    @Test
    public void testEmptyArray() throws IOException, BadRequestException
    {
        String json = "[]";

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));

    }

    @Test(expected = JsonParseException.class)
    public void testInvalidJson() throws IOException, BadRequestException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"foobar, \"params\": \"bar\", \"baz]";

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));

    }

    @Test
    public void testInvalidRequest() throws IOException, BadRequestException
    {

        String json = "{\"jsonrpc\": \"2.0\", \"method\": 1, \"params\": \"bar\"}";

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        assertThat(request.getInvocations().size(), is(1));
        assertThat(request.getInvocations().get(0).isError(), is(true));
        assertThat(request.getInvocations().get(0).getError(), instanceOf(JsonRpcProtocol.InvalidJsonRpcRequest.class));
    }


    @Test
    public void testBatchWithInvalidItems() throws IOException, BadRequestException
    {
        String json = "[1,{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1},2,3]";

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
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

    @Test
    public void testResponse() throws IOException, BadRequestException, NoSuchMethodException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"1\"}";

        List<Method> methods = ReflectionUtils.findMethods(Calculator.class, "subtract");

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));

        protocol.resolveMethod(methods, request.getInvocations().get(0), request);

        request.getInvocations().get(0).setInvocationResult(1);


        MockRpcResponse response = new MockRpcResponse();
        protocol.writeResponse(response, request);

        assertThat(response.responseContent(), is("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":1}"));
    }

    @Test
    public void testStringResponse() throws IOException, BadRequestException, NoSuchMethodException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"getName\", \"params\": [], \"id\": \"1\"}";

        List<Method> methods = ReflectionUtils.findMethods(Calculator.class, "getName");

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));

        protocol.resolveMethod(methods, request.getInvocations().get(0), request);

        request.getInvocations().get(0).setInvocationResult("aaa");


        MockRpcResponse response = new MockRpcResponse();
        protocol.writeResponse(response, request);

        assertThat(response.responseContent(), is("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"aaa\"}"));
    }

    @Test
    public void testBatchResponse() throws IOException, BadRequestException, NoSuchMethodException
    {
        String json = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"1\"}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"add\", \"params\": [42, 23], \"id\": \"2\"}]";


        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));
        List<Method> methods = ReflectionUtils.findMethods(Calculator.class, "subtract");
        protocol.resolveMethod(methods, request.getInvocations().get(0), request);

        methods = ReflectionUtils.findMethods(Calculator.class, "add");
        protocol.resolveMethod(methods, request.getInvocations().get(1), request);


        request.getInvocations().get(0).setInvocationResult(1);
        request.getInvocations().get(1).setInvocationResult(2);


        MockRpcResponse response = new MockRpcResponse();
        protocol.writeResponse(response, request);

        assertThat(response.responseContent(), is("[{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":1},{\"jsonrpc\":\"2.0\",\"id\":\"2\",\"result\":2}]"));
    }


    @Test
    public void testErrorResponse() throws IOException, BadRequestException, NoSuchMethodException
    {
        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"1\"}";

        List<Method> methods = ReflectionUtils.findMethods(Calculator.class, "subtract");

        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));

        protocol.resolveMethod(methods, request.getInvocations().get(0), request);

        request.getInvocations().get(0).setError(new CalculatorException("test"));


        MockRpcResponse response = new MockRpcResponse();
        protocol.writeResponse(response, request);

        assertThat(response.responseContent(), startsWith("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32603,\"message\":\"test\",\"data\":{\"cause\":null,\"stackTrace\":[{\"methodName\":\"testErrorResponse\","));
        // i want to ignore the line number in the stack-trace
        assertThat(response.responseContent(), containsString("\"fileName\":\"JsonRpcProtocolParserTest.java\""));
        assertThat(response.responseContent(), containsString("\"className\":\"org.wixpress.fjarr.json.JsonRpcProtocolParserTest\""));
        assertThat(response.responseContent(), containsString("\"message\":\"test\",\"localizedMessage\":\"test\""));
    }

    @Test
    public void testBatchWithErrorResponse() throws IOException, BadRequestException, NoSuchMethodException
    {
        String json = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": \"1\"}," +
                "{\"jsonrpc\": \"2.0\", \"method\": \"add\", \"params\": [42, 23], \"id\": \"2\"}]";


        RpcRequest r = mock(RpcRequest.class);
        ParsedRpcRequest request = protocol.parseRequestJson(r, mapper.readTree(json));

        List<Method> methods = ReflectionUtils.findMethods(Calculator.class, "subtract");
        protocol.resolveMethod(methods, request.getInvocations().get(0), request);

        methods = ReflectionUtils.findMethods(Calculator.class, "add");

        protocol.resolveMethod(methods, request.getInvocations().get(1), request);

        request.getInvocations().get(0).setInvocationResult(1);
        request.getInvocations().get(1).setError(new CalculatorException("test"));


        MockRpcResponse response = new MockRpcResponse();
        protocol.writeResponse(response, request);

        assertThat(response.responseContent(), startsWith("[{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":1},{\"jsonrpc\":\"2.0\",\"id\":\"2\",\"error\":{\"code\":-32603,\"message\":\"test\",\"data\":{\"cause\":null,\"stackTrace\":[{\"methodName\":\"testBatchWithErrorResponse\","));
        // i want to ignore the line number in the stack-trace
        assertThat(response.responseContent(), containsString("\"fileName\":\"JsonRpcProtocolParserTest.java\""));
        assertThat(response.responseContent(), containsString("\"className\":\"org.wixpress.fjarr.json.JsonRpcProtocolParserTest\""));
        assertThat(response.responseContent(), containsString("\"message\":\"test\",\"localizedMessage\":\"test\""));
    }


    public static interface Calculator
    {

        int subtract(int i, int j);

        public int add(int i, int j);

        public long subtract(long i, long j);

        public long add(long i, long j);

        public String getName();
    }

    public static class CalculatorException extends Exception
    {
        public CalculatorException(String message)
        {
            super(message);
        }
    }

}
