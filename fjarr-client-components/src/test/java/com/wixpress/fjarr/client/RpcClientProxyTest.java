package com.wixpress.fjarr.client;

import com.wixpress.fjarr.example.DataStruct;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.util.MultiMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


/**
 * @author AlexeyR
 * @since 12/10/12 5:07 PM
 */

public class RpcClientProxyTest {

    private final RpcClientProtocol protocolClient = mock(RpcClientProtocol.class);
    private final RpcInvoker invoker = mock(RpcInvoker.class);
    private DataStructService dss;

    @Before
    public void init() throws URISyntaxException {
        when(protocolClient.getAcceptType()).thenReturn("accept");
        when(protocolClient.getContentType()).thenReturn("content");
        dss = RpcClientProxy.create(DataStructService.class, "www.example.com", invoker, protocolClient);
    }

    @Test
    public void testProxyWithRemote() throws Throwable {

        final String methodName = "getData";
        final String requestBody = "request";
        final String responseBody = "response";

        final DataStruct ds = new DataStruct(1, "a", 0.1, UUID.randomUUID());
        when(protocolClient.writeRequest(methodName, new Object[]{})).thenReturn(requestBody);
        when(protocolClient.readResponse(DataStruct.class, responseBody)).thenReturn(ds);

        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));


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
    public void testProxyWithLocal() throws Throwable {
        final String methodName = "getData";
        final String requestBody = "request";
        final String responseBody = "response";

        final DataStruct ds = new DataStruct(1, "a", 0.1, UUID.randomUUID());
        when(protocolClient.writeRequest(methodName, new Object[]{})).thenReturn(requestBody);
        when(protocolClient.readResponse(DataStruct.class, responseBody)).thenReturn(ds);

        when(invoker.invoke(any(RpcInvocation.class))).thenReturn(new RpcInvocationResponse(200, "", responseBody,
                new MultiMap<String, String>()
                        .with("header", "value")));


        String s = dss.toString();

        InOrder io = inOrder(protocolClient, invoker);
        io.verifyNoMoreInteractions();

    }

}
