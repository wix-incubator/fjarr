package com.wixpress.fjarr.client;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.wixpress.fjarr.matcher.HeaderMatcher.isHeader;
import static com.wixpress.fjarr.matchers.IsMultiMapcontining.hasEntry;
import static com.wixpress.fjarr.matchers.UriMatcher.isUri;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author alex
 * @since 1/3/13 5:31 PM
 */

public class InvokerTest
{


    private final ClientConnectionManager connectionManager = mock(ClientConnectionManager.class);
    private final HttpClient client = mock(HttpClient.class);

    HttpComponentsInvoker invoker = new HttpComponentsInvoker(new MockHttpClientFactory());

    @Before
    public void init()
    {
        reset(client, connectionManager);
        when(client.getConnectionManager()).thenReturn(connectionManager);
    }


    @Test
    public void testInvokeSuccess() throws URISyntaxException, IOException
    {

        final RpcInvocation invocation = new RpcInvocation(new URI("www.example.com"), "request body")
                .withHeader("Test-Header", "header value")
                .withContentType("application/example")
                .withQueryParameter("a", "b")
                .withQueryParameter("a b", "b c");

        when(client.execute(any(HttpPost.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                assertTrue(invocationOnMock.getArguments()[0] instanceof HttpPost);

                HttpPost post = (HttpPost) invocationOnMock.getArguments()[0];

                assertThat(post.getAllHeaders(), allOf(hasItemInArray(isHeader("Test-Header", "header value")),
                        hasItemInArray(isHeader("Connection", "close"))));

                assertThat(post.getURI(), isUri(new URI("www.example.com?a+b=b+c&a=b")));


                final BasicHttpResponse response = new BasicHttpResponse(
                        new BasicStatusLine(HttpVersion.HTTP_1_0, 200, "OK"));
                response.setHeader("Content-Type", "application/example");
                response.setHeader("Test1", "test1");
                response.setHeader("Test2", "test2");
                response.setEntity(new StringEntity("response"));
                return response;


            }
        });


        final RpcInvocationResponse response = invoker.invoke(invocation);
        assertThat(response.getBody(), is("response"));
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getStatusDescription(), is("OK"));
        assertThat(response.getAllHeaders(), allOf(hasEntry("Test1", "test1"),
                hasEntry("Test2", "test2")));

        InOrder io = inOrder(client, connectionManager);
        io.verify(client).execute(any(HttpPost.class));
        io.verify(connectionManager).shutdown();
        io.verifyNoMoreInteractions();

    }


    @Test
    public void testInvokeFailure() throws URISyntaxException, IOException
    {
        final RpcInvocation invocation = new RpcInvocation(new URI("www.example.com"), "request body")
                .withHeader("Test-Header", "header value")
                .withContentType("application/example")
                .withQueryParameter("a", "b")
                .withQueryParameter("a b", "b c");

        when(client.execute(any(HttpPost.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                assertTrue(invocationOnMock.getArguments()[0] instanceof HttpPost);

                HttpPost post = (HttpPost) invocationOnMock.getArguments()[0];

                assertThat(post.getAllHeaders(), allOf(
                        hasItemInArray(isHeader("Test-Header", "header value")),
                        hasItemInArray(isHeader("Connection", "close"))));

                assertThat(post.getURI(), isUri(new URI("www.example.com?a+b=b+c&a=b")));


                final BasicHttpResponse response = new BasicHttpResponse(
                        new BasicStatusLine(HttpVersion.HTTP_1_0, 500, "Server Error"));
                response.setHeader("Content-Type", "application/example; charset=utf-8");
                response.setHeader("Test1", "test1");
                response.setHeader("Test2", "test2");
                response.setEntity(new StringEntity("response"));
                return response;


            }
        });


        final RpcInvocationResponse response = invoker.invoke(invocation);
        assertThat(response.getBody(), is("response"));
        assertThat(response.getStatusCode(), is(500));
        assertThat(response.getStatusDescription(), is("Server Error"));
        assertThat(response.getAllHeaders(), allOf(hasEntry("Test1", "test1"),
                hasEntry("Test2", "test2")));

        InOrder io = inOrder(client, connectionManager);
        io.verify(client).execute(any(HttpPost.class));
        io.verify(connectionManager).shutdown();
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testInvokeException() throws URISyntaxException, IOException
    {
        final RpcInvocation invocation = new RpcInvocation(new URI("www.example.com"), "request body")
                .withHeader("Test-Header", "header value")
                .withContentType("application/example")
                .withQueryParameter("a", "b")
                .withQueryParameter("a b", "b c");

        when(client.execute(any(HttpPost.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                assertTrue(invocationOnMock.getArguments()[0] instanceof HttpPost);

                HttpPost post = (HttpPost) invocationOnMock.getArguments()[0];

                assertThat(post.getAllHeaders(), allOf(hasItemInArray(isHeader("Test-Header", "header value")),
                        hasItemInArray(isHeader("Connection", "close"))));

                assertThat(post.getURI(), isUri(new URI("www.example.com?a+b=b+c&a=b")));


                throw new IOException("test test test");


            }
        });


        try
        {
            invoker.invoke(invocation);
            fail("shouldn't get here");
        }
        catch (IOException e)
        {
            assertThat(e.getMessage(), is("test test test"));
        }


        InOrder io = inOrder(client, connectionManager);
        io.verify(client).execute(any(HttpPost.class));
        io.verify(connectionManager).shutdown();
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testInvokeEmptyResponse() throws URISyntaxException, IOException
    {
        final RpcInvocation invocation = new RpcInvocation(new URI("www.example.com"), "request body")
                .withHeader("Test-Header", "header value")
                .withContentType("application/example")
                .withQueryParameter("a", "b")
                .withQueryParameter("a b", "b c");

        when(client.execute(any(HttpPost.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                assertTrue(invocationOnMock.getArguments()[0] instanceof HttpPost);

                HttpPost post = (HttpPost) invocationOnMock.getArguments()[0];

                assertThat(post.getAllHeaders(), allOf(hasItemInArray(isHeader("Test-Header", "header value")),
                        hasItemInArray(isHeader("Connection", "close"))));

                assertThat(post.getURI(), isUri(new URI("www.example.com?a+b=b+c&a=b")));

                final BasicHttpResponse response = new BasicHttpResponse(
                        new BasicStatusLine(HttpVersion.HTTP_1_0, 500, "Server Error"));
                response.setHeader("Content-Type", "application/example");
                response.setHeader("Test1", "test1");
                response.setHeader("Test2", "test2");
                response.setEntity(null);
                return response;
            }
        });


        try
        {
            invoker.invoke(invocation);
            fail("shouldn't get here");
        }
        catch (IOException e)
        {
            assertThat(e.getMessage(), is("Empty HTTP response"));
        }


        InOrder io = inOrder(client, connectionManager);
        io.verify(client).execute(any(HttpPost.class));
        io.verify(connectionManager).shutdown();
        io.verifyNoMoreInteractions();
    }


    @Test
    public void testInvokeEmptyResponseEncoding() throws URISyntaxException, IOException
    {
        final RpcInvocation invocation = new RpcInvocation(new URI("www.example.com"), "request body")
                .withHeader("Test-Header", "header value")
                .withContentType("application/example; charset=utf-8")
                .withQueryParameter("a", "b")
                .withQueryParameter("a b", "b c");

        when(client.execute(any(HttpPost.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                assertTrue(invocationOnMock.getArguments()[0] instanceof HttpPost);

                HttpPost post = (HttpPost) invocationOnMock.getArguments()[0];

                assertThat(post.getAllHeaders(), allOf(hasItemInArray(isHeader("Test-Header", "header value")),
                        hasItemInArray(isHeader("Connection", "close"))));

                assertThat(post.getURI(), isUri(new URI("www.example.com?a+b=b+c&a=b")));

                final BasicHttpResponse response = new BasicHttpResponse(
                        new BasicStatusLine(HttpVersion.HTTP_1_0, 500, "Server Error"));
                response.setHeader("Content-Type", "application/example");
                response.setHeader("Test1", "test1");
                response.setHeader("Test2", "test2");
                response.setEntity(new StringEntity("response"));
                return response;
            }
        });


        final RpcInvocationResponse response = invoker.invoke(invocation);
        assertThat(response.getBody(), is("response"));
        assertThat(response.getStatusCode(), is(500));
        assertThat(response.getStatusDescription(), is("Server Error"));
        assertThat(response.getAllHeaders(), allOf(hasEntry("Test1", "test1"),
                hasEntry("Test2", "test2")));

        InOrder io = inOrder(client, connectionManager);
        io.verify(client).execute(any(HttpPost.class));
        io.verify(connectionManager).shutdown();
        io.verifyNoMoreInteractions();
    }


    private class MockHttpClientFactory implements HttpClientFactory
    {

        @Override
        public HttpClient createHttpClient()
        {
            return client;
        }

        @Override
        public boolean useConnectionPool()
        {
            return false;
        }
    }
}
