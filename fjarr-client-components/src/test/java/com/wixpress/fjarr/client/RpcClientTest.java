package com.wixpress.fjarr.client;

import com.wixpress.fjarr.client.exceptions.InvalidRpcResponseException;
import com.wixpress.fjarr.client.exceptions.RpcInvocationException;
import com.wixpress.fjarr.client.exceptions.RpcTransportException;
import com.wixpress.fjarr.util.MultiMap;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


/**
 * @author AlexeyR
 * @since 12/10/12 5:07 PM
 */

public class RpcClientTest {

    public static final String TEST_METHOD = "testMethod";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    private final int statusCode = 200;
    private final String statusDescription = "OK";

    private String methodName = TEST_METHOD;
    private String requestBody = REQUEST;
    private String responseBody = RESPONSE;
    private List<Integer> integers = Arrays.asList(1);

    private final RpcClientProtocol protocolClient = mock(RpcClientProtocol.class);
    private final RpcInvoker invoker = mock(RpcInvoker.class);
    private RpcClient client;

    @Before
    public void init() throws URISyntaxException {
        when(protocolClient.getAcceptType()).thenReturn("accept");
        when(protocolClient.getContentType()).thenReturn("content");

        client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);
    }

    @Test
    public void testClient() throws Throwable {
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(protocolClient.readResponse(List.class, responseBody)).thenReturn(integers);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(aDefaultRpcInvocationResponse());


        Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        assertThat(o, instanceOf(List.class));
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verify(protocolClient).readResponse(List.class, responseBody);
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientHttpError() throws Throwable {
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(anRpcInvocationResponseWithStatus(400, "ERROR"));
        when(invoker.toString()).thenReturn("MockInvoker");


        try {

            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        } catch (RpcTransportException e) {
            assertThat(e.getStatusCode(), is(400));
            assertThat(e.getMessage(), Matchers.startsWith("Unexpected server HTTP status code [endpoint={url='www.example.com',service='testService',method='testMethod'},httpStatusLine='400 ERROR'"));
        } catch (Throwable t) {
            fail("Shouldn't throw this exception");
        }
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(protocolClient).getAcceptType();
        io.verify(protocolClient).getContentType();
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientEmptyResponse() throws Throwable {
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(anRpcInvocationResponseWithResponseBody(""));
        when(invoker.toString()).thenReturn("MockInvoker");

        try {

            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        } catch (RpcTransportException e) {
            assertThat(e.getStatusCode(), is(statusCode));
            assertThat(e.getMessage(), Matchers.startsWith("Empty HTTP response [endpoint={url='www.example.com',service='testService',method='testMethod'},httpStatusLine='200 OK'"));
        } catch (Throwable t) {
            fail("Shouldn't throw this exception");
        }
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody)
                .withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(protocolClient).getAcceptType();
        io.verify(protocolClient).getContentType();
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientInvokerException() throws Throwable {
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        Throwable mockInvokerException = new RuntimeException("MockInvokerException");
        when(invoker.invoke(any(RpcInvocation.class))).thenThrow(mockInvokerException);
        when(invoker.toString()).thenReturn("MockInvoker");

        try {

            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        } catch (RpcTransportException e) {
            assertThat(e.getStatusCode(), is(0));
            assertThat(e.getMessage(), Matchers.startsWith("Exception while communicating with server [endpoint={url='www.example.com',service='testService',method='testMethod'}"));
            assertThat(e.getCause(), sameInstance(mockInvokerException));
        } catch (Throwable t) {
            fail("Shouldn't throw this exception");
        }
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(protocolClient).getAcceptType();
        io.verify(protocolClient).getContentType();
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientWriteRequest() throws Throwable {
        Throwable mockProtocolException = new RuntimeException("MockProtocolException");
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenThrow(mockProtocolException);

        try {

            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        } catch (Throwable t) {
            assertThat(t, sameInstance(mockProtocolException));
        }
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientReadResponseException() throws Throwable {
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        Throwable mockProtocolException = new RpcInvocationException("MockInvocation");
        when(protocolClient.readResponse(List.class, responseBody)).thenThrow(mockProtocolException);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(aDefaultRpcInvocationResponse());

        try {

            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        } catch (Throwable t) {
            assertThat(t, sameInstance(mockProtocolException));
        }
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(protocolClient).getAcceptType();
        io.verify(protocolClient).getContentType();
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verify(protocolClient).readResponse(List.class, responseBody);
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientInvalidResponseException() throws Throwable {
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        Throwable mockProtocolException = new InvalidRpcResponseException("MockInvocation");
        when(protocolClient.readResponse(List.class, responseBody)).thenThrow(mockProtocolException);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(aDefaultRpcInvocationResponse());

        try {

            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        } catch (Throwable t) {
            assertThat(t, sameInstance(mockProtocolException));
        }
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(protocolClient).getAcceptType();
        io.verify(protocolClient).getContentType();
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verify(protocolClient).readResponse(List.class, responseBody);
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientWithMethod() throws Throwable {
        methodName = "t1";
        Method m = this.getClass().getMethod(methodName, Integer.class);
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(protocolClient.readResponse(m.getGenericReturnType(), responseBody)).thenReturn(integers);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(aDefaultRpcInvocationResponse());

        Object o = client.invoke(m, 1, 2, 3);

        assertThat(o, instanceOf(List.class));
        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                .withContentType("content");
        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verify(protocolClient).readResponse(m.getGenericReturnType(), responseBody);
        io.verifyNoMoreInteractions();
    }

    //Used by testClientWithMethod
    public List<String> t1(Integer a) {
        return null;
    }

    private RpcInvocationResponse aDefaultRpcInvocationResponse() {
        return anRpcInvocationResponseWith(statusCode, statusDescription, responseBody);
    }

    private RpcInvocationResponse anRpcInvocationResponseWithStatus(int statusCode, String statusDescription) {
        return anRpcInvocationResponseWith(statusCode, statusDescription, responseBody);
    }

    private RpcInvocationResponse anRpcInvocationResponseWithResponseBody(String responseBody) {
        return anRpcInvocationResponseWith(statusCode, statusDescription, responseBody);
    }

    private RpcInvocationResponse anRpcInvocationResponseWith(int statusCode, String statusDescription, String responseBody) {
        return new RpcInvocationResponse(statusCode, statusDescription, responseBody,
                new MultiMap<String, String>()
                        .with("header", "value"));
    }


}
