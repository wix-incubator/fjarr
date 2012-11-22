package com.wixpress.framework.rpc.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wixpress.framework.rpc.client.exceptions.InvalidRpcResponseException;
import com.wixpress.framework.rpc.client.exceptions.RpcInvocationException;
import com.wixpress.framework.rpc.server.RpcInvocation;
import com.wixpress.hoopoe.reflection.parameters.AnnotationParameterNameDiscoverer;
import com.wixpress.hoopoe.reflection.parameters.ParameterNameDiscoverer;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author alex
 * @since 10/25/11 3:50 PM
 */

public class JsonExceptionTest {

    final String ERROR_TEMPLATE = "{\n" +
            "    \"error\": {\n" +
            "        \"code\": -1,\n" +
            "        \"message\": \"Error Message\",\n" +
            "        \"data\": %s\n" +
            "    }\n" +
            "}";


    private ObjectMapper mapper = createObjectMapper();

    private ObjectMapper createObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new HoopoeRpcJacksonModule());
        return objectMapper;
    }

    private ParameterNameDiscoverer d = new AnnotationParameterNameDiscoverer();
    JsonRpcProtocol protocol = new JsonRpcProtocol(mapper, d);

    JsonRpcProtocolClient cProtocol = new JsonRpcProtocolClient(mapper);

    @Test
    public void testCheckedDeclaredException() throws NoSuchMethodException {
        RpcInvocation i = new RpcInvocation(new DummyException("message", new DummyInnerException("b", 1)));
        i.setResolvedMethod(this.getClass().getMethod("testMethod"));
        ObjectNode r = protocol.createErrorResponse(i);
        Exception e = null;

        try {
            e = (Exception) mapper.readValue(mapper.treeAsTokens(r.get("data")), Exception.class);
        } catch (Exception e1) {

        }
        assertEquals(e.getClass(), DummyException.class);
        assertThat(e.getMessage(), is("message"));
        assertThat(e.getCause(), nullValue());
    }

    @Test
    public void testCheckedUnDeclaredException() throws NoSuchMethodException {
        RpcInvocation i = new RpcInvocation(new Dummy2Exception("message-A", new DummyInnerException("message-B", 1)));
        i.setResolvedMethod(this.getClass().getMethod("testMethod"));
        ObjectNode r = protocol.createErrorResponse(i);
        assertThat(r.get("message").textValue(), allOf(containsString("message-A"),containsString("DummyInnerException"), containsString("message-B")));
        Exception e = null;

        try {
            e = (Exception) mapper.readValue(mapper.treeAsTokens(r.get("data")), Exception.class);
        } catch (Exception e1) {

        }
        assertEquals(e.getClass(), Dummy2Exception.class);
        assertThat(e.getStackTrace()[0].toString(), is("com.wixpress.framework.rpc.json.JsonExceptionTest.testCheckedUnDeclaredException(JsonExceptionTest.java:69)"));
        assertThat(e.getCause(), nullValue());
    }

    public void testMethod() throws DummyException {

    }

    @Test
    public void deserializeException() throws IOException {

        String exceptionJson = "{\"@class\": \"com.wixpress.framework.rpc.json.DummyException\", \"message\": \"Exception Message\"}";
        String json = String.format(ERROR_TEMPLATE, exceptionJson);

        try {
            cProtocol.readResponse(Object.class, new Class<?>[0], new StringReader(json));

        } catch (RpcInvocationException e) {
            assertThat(e.getServerException(), instanceOf(DummyException.class));
            assertThat(e.getServerException().getMessage(), is("Exception Message"));
            assertThat(e.getStackTrace()[0].getMethodName(), is("handleExceptionFromServer"));
        }
    }

    @Test
    public void deserializeMissingException() throws IOException {

        String exceptionJson = "{\"@class\": \"com.wixpress.framework.rpc.json.FooException\", \"message\": \"Exception Message\"}";
        String json = String.format(ERROR_TEMPLATE, exceptionJson);

        try {
            cProtocol.readResponse(Object.class, new Class<?>[0], new StringReader(json));

        } catch (RpcInvocationException e) {
            assertFalse(e.hasServerException());
        }
    }

    @Test
    public void deserializeInvalidResponse() throws IOException {

        String json = "{\"a\":\"b\"}";

        try {
            cProtocol.readResponse(Object.class, new Class<?>[0], new StringReader(json));

        } catch (InvalidRpcResponseException e) {
            assertThat(e.getMessage(), containsString(json));
        }
    }
}
