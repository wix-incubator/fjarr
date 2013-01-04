package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.IOUtils;
import com.wixpress.fjarr.util.MultiMap;
import com.wixpress.fjarr.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author alex
 * @since 1/3/13 11:25 AM
 */

public class HttpComponentsInvoker implements RpcInvoker
{

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpComponentsInvoker.class);
    private static final BasicHeader CONNECTION_CLOSE_HEADER = new BasicHeader("Connection", "close");
    private final HttpClientFactory factory;

    public HttpComponentsInvoker(HttpClientFactory factory)
    {

        this.factory = factory;
    }

    @Override
    public RpcInvocationResponse invoke(RpcInvocation invocation) throws IOException
    {
        HttpClient httpClient = factory.createHttpClient();
        try
        {
            HttpPost p = new HttpPost(invocation.getServiceUri()
            );
            if (!factory.useConnectionPool())
            {
                p.addHeader(CONNECTION_CLOSE_HEADER);
            }

            StringEntity entity = new StringEntity(invocation.getBody(), invocation.getContentType(), invocation.getCharacterEncoding());
            p.setEntity(entity);
            for (Map.Entry<String, String> entry : invocation.getAllHeaders().entrySet())
            {
                p.addHeader(entry.getKey(), entry.getValue());
            }


            HttpResponse response = null;

            try
            {
                response = httpClient.execute(p);
            }
            catch (IOException e)
            {
                p.abort();

                throw e;
            }


            HttpEntity responseEntity = response.getEntity();
// read the response

            if (responseEntity == null)
            {
                p.abort();
                throw new IOException("Empty HTTP response");
            }

            String body;

            try
            {
                final Header contentEncoding = responseEntity.getContentEncoding();
                if (contentEncoding != null && StringUtils.isNotBlank(contentEncoding.getValue()))
                    body = IOUtils.toString(responseEntity.getContent(), contentEncoding.getValue());
                else
                    body = IOUtils.toString(responseEntity.getContent(), invocation.getCharacterEncoding());
            }
            catch (IOException e)
            {
                p.abort();
                throw e;
            }
            finally
            {
                safeConsumeResponseEntity(responseEntity);
            }
            return new RpcInvocationResponse(response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(),
                    body, transformHeaders(response.getAllHeaders()));

        }

        finally

        {
            httpClient.getConnectionManager().shutdown();
            //            connectionManager.closeExpiredConnections();
            //            connectionManager.closeIdleConnections(5, TimeUnit.SECONDS);
        }

    }


    private MultiMap<String, String> transformHeaders(Header[] allHeaders)
    {
        MultiMap<String, String> mmHeaders = new MultiMap<String, String>();
        for (Header header : allHeaders)
        {
            mmHeaders.put(header.getName(), header.getValue());
        }
        return mmHeaders;
    }


    private void safeConsumeResponseEntity(HttpEntity responseEntity)
    {
        try
        {
            EntityUtils.consume(responseEntity);
        }
        catch (IOException e)
        {
            // swallow
        }
    }

}
