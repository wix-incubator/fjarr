package com.wixpress.fjarr.client;

import com.wixpress.fjarr.client.exceptions.InvalidRpcResponseException;
import com.wixpress.fjarr.client.exceptions.RpcInvocationException;
import com.wixpress.fjarr.client.exceptions.RpcTransportException;
import com.wixpress.fjarr.example.DataStruct;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.util.MultiMap;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

public class RpcClientTest
{

    private final RpcClientProtocol protocolClient = mock(RpcClientProtocol.class);

    @Before
    public void init()
    {
        reset(protocolClient);
        when(protocolClient.getAcceptType()).thenReturn("accept");
        when(protocolClient.getContentType()).thenReturn("content");

    }

    @Test
    public void testClient() throws Throwable
    {
        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(protocolClient.readResponse(List.class, responseBody)).thenReturn(integers);

        RpcInvoker invoker = mock(RpcInvoker.class);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));

        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

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
    public void testClientHttpError() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);

        RpcInvoker invoker = mock(RpcInvoker.class);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(400, "ERROR", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));
        when(invoker.toString()).thenReturn("MockInvoker");

        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

        try
        {
            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);
        }
        catch (RpcTransportException e)
        {

            assertThat(e.getStatusCode(), is(400));
            assertThat(e.getMessage(), Matchers.startsWith("Unexpected server HTTP status code [endpoint={url='www.example.com',service='testService',method='testMethod'},httpStatusLine='400 ERROR'"));
        }
        catch (Throwable t)
        {
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
    public void testClientEmptyResponse() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);


        RpcInvoker invoker = mock(RpcInvoker.class);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "OK", "       ",
                new MultiMap<String, String>()
                        .with("header", "value")));
        when(invoker.toString()).thenReturn("MockInvoker");

        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

        try
        {
            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);
        }
        catch (RpcTransportException e)
        {

            assertThat(e.getStatusCode(), is(200));
            assertThat(e.getMessage(), Matchers.startsWith("Empty HTTP response [endpoint={url='www.example.com',service='testService',method='testMethod'},httpStatusLine='200 OK'"));
        }
        catch (Throwable t)
        {
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
    public void testClientInvokerException() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);

        RpcInvoker invoker = mock(RpcInvoker.class);

        Throwable mockInvokerException = new RuntimeException("MockInvokerException");

        when(invoker.invoke(any(RpcInvocation.class))).thenThrow(mockInvokerException);
        when(invoker.toString()).thenReturn("MockInvoker");

        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

        try
        {
            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);
        }
        catch (RpcTransportException e)
        {

            assertThat(e.getStatusCode(), is(0));
            assertThat(e.getMessage(), Matchers.startsWith("Exception while communicating with server [endpoint={url='www.example.com',service='testService',method='testMethod'}"));
            assertThat(e.getCause(), sameInstance(mockInvokerException));
        }
        catch (Throwable t)
        {
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
    public void testClientWriteRequest() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        Throwable mockProtocolException = new RuntimeException("MockProtocolException");
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenThrow(mockProtocolException);

        RpcInvoker invoker = mock(RpcInvoker.class);


        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

        try
        {
            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);

        }
        catch (Throwable t)
        {
            assertThat(t, sameInstance(mockProtocolException));
        }

        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1, 2, 3});
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testClientReadResponseException() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);

        RpcInvoker invoker = mock(RpcInvoker.class);

        Throwable mockProtocolException = new RpcInvocationException("MockInvocation");


        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(protocolClient.readResponse(List.class, responseBody)).thenThrow(mockProtocolException);

        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));


        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

        try
        {
            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);
        }
        catch (Throwable t)
        {
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
    public void testClientInvalidResponseException() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        final String methodName = "testMethod";

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);

        RpcInvoker invoker = mock(RpcInvoker.class);

        Throwable mockProtocolException = new InvalidRpcResponseException("MockInvocation");


        when(protocolClient.writeRequest(methodName, new Object[]{1, 2, 3})).thenReturn(requestBody);
        when(protocolClient.readResponse(List.class, responseBody)).thenThrow(mockProtocolException);

        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));


        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);

        try
        {
            Object o = client.invoke("testService", methodName, List.class, 1, 2, 3);
        }
        catch (Throwable t)
        {
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
    public void testClientWithMethod() throws Throwable
    {

        ArrayList<Integer> integers = new ArrayList<Integer>();

        Method m = this.getClass().getMethod("t1", Integer.class);

        final String methodName = m.getName();

        integers.add(1);
        final String requestBody = "request";
        final String responseBody = "response";
        when(protocolClient.writeRequest(methodName, new Object[]{1})).thenReturn(requestBody);
        when(protocolClient.readResponse(m.getGenericReturnType(), responseBody)).thenReturn(integers);

        RpcInvoker invoker = mock(RpcInvoker.class);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));

        RpcClient client = new RpcClient(new URI("www.example.com"), protocolClient, invoker);


        Object o = client.invoke(m, 1);
        assertThat(o, instanceOf(List.class));

        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{1});

        final RpcInvocation rpcInvocation = new RpcInvocation(client.getServiceUrl(), requestBody).withHeader("Accept", "accept")
                               .withContentType("content");

        io.verify(invoker).invoke(eq(rpcInvocation));
        io.verify(protocolClient).readResponse(m.getGenericReturnType(), responseBody);

        io.verifyNoMoreInteractions();
    }


    @Test
    public void testProxyWithRemote() throws Throwable
    {

        final String methodName = "getData";
        final String requestBody = "request";
        final String responseBody = "response";

        final DataStruct ds = new DataStruct(1, "a", 0.1, UUID.randomUUID());
        when(protocolClient.writeRequest(methodName, new Object[]{})).thenReturn(requestBody);
        when(protocolClient.readResponse(DataStruct.class, responseBody)).thenReturn(ds);

        RpcInvoker invoker = mock(RpcInvoker.class);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));


        DataStructService dss = RpcClientProxy.create(DataStructService.class, "www.example.com", invoker, protocolClient);

        DataStruct actual = dss.getData();
        assertThat(actual, sameInstance(ds));

        InOrder io = inOrder(protocolClient, invoker);
        io.verify(protocolClient).writeRequest(methodName, new Object[]{});
        final RpcInvocation invocation = new RpcInvocation(new URI("www.example.com"), requestBody).withHeader("Accept", "accept")
                                       .withContentType("content");
        io.verify(invoker).invoke(eq(invocation));
        io.verify(protocolClient).readResponse(DataStruct.class, responseBody);
        io.verifyNoMoreInteractions();

    }


    @Test
    public void testProxyWithLocal() throws Throwable
    {
        final String methodName = "getData";
        final String requestBody = "request";
        final String responseBody = "response";

        final DataStruct ds = new DataStruct(1, "a", 0.1, UUID.randomUUID());
        when(protocolClient.writeRequest(methodName, new Object[]{})).thenReturn(requestBody);
        when(protocolClient.readResponse(DataStruct.class, responseBody)).thenReturn(ds);

        RpcInvoker invoker = mock(RpcInvoker.class);
        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));


        DataStructService dss = RpcClientProxy.create(DataStructService.class, "www.example.com", invoker, protocolClient);


        String s = dss.toString();

        InOrder io = inOrder(protocolClient, invoker);
        io.verifyNoMoreInteractions();

    }


    public List<String> t1(Integer a)
    {
        return null;
    }


}
