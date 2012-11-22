package com.wixpress.framework.rpc.client;

import com.wixpress.framework.rpc.client.exceptions.RpcTransportException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.wixpress.framework.rpc.client.HttpClientConfig.defaults;
import static java.lang.String.format;

/**
 * @author alexeyr
 * @since 6/29/11 4:16 PM
 */

public class RpcOverHttpClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcOverHttpClient.class);
    private static final BasicHeader CONNECTION_CLOSE_HEADER = new BasicHeader("Connection", "close");

    private URI serviceUrl;

    private final RpcProtocolClient protocol;
    //    private final HttpClient httpClient;
    private final HttpClientConfig config;
//    private final ClientConnectionManager connectionManager;

    public RpcOverHttpClientEventHandler eventHandler;

    public RpcOverHttpClient(URI serviceUrl, RpcProtocolClient protocol)
    {
        this(serviceUrl, protocol,  defaults(),(RpcOverHttpClientEventHandler) null);
    }

    public RpcOverHttpClient(URI serviceUrl, RpcProtocolClient protocol, HttpClientConfig config)
    {
        this(serviceUrl, protocol, config, (RpcOverHttpClientEventHandler) null);
    }

    public RpcOverHttpClient(URI serviceUrl, RpcProtocolClient protocol, RpcOverHttpClientEventHandler eventHandler)
    {
        this(serviceUrl, protocol,defaults(),eventHandler);
    }

    public RpcOverHttpClient(URI serviceUrl, RpcProtocolClient protocol, HttpClientConfig config, RpcOverHttpClientEventHandler eventHandler)
    {
        checkNotNull(serviceUrl);
        checkNotNull(protocol);
        checkNotNull(config);

        LOGGER.info(format("Configuring RpcOverHttpClient [serviceUrl='%s',config=%s]", serviceUrl, config));

        this.serviceUrl = serviceUrl;
        this.protocol = protocol;
        this.config = config;
        this.eventHandler = eventHandler;
    }



    private HttpClient createHttpClient()
    {
        DefaultHttpClient hc = new DefaultHttpClient(buildConnectionManager());

        HttpParams params = hc.getParams();
        HttpConnectionParams.setConnectionTimeout(params, config.getConnectionTimeoutMillis());
        HttpConnectionParams.setSoTimeout(params, config.getSocketTimeoutMillis());
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
        return hc;
    }

    private ClientConnectionManager buildConnectionManager()
    {
        // init the http client
        return new SingleClientConnManager();
    }


    public Object invoke(Method method, Object[] arguments) throws Throwable
    {
        HttpClient httpClient = createHttpClient();
        try
        {
            for (int attempt = 0; ; ++attempt)
            {
                try
                {
                    return _invoke(method, arguments, httpClient);
                }
                catch (Exception e)
                {
                        throw e;
                }
            }
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private Object _invoke(Method method, Object[] arguments, HttpClient httpClient) throws Throwable
    {

        HttpPost p = new HttpPost(serviceUrl);
        if (!config.isUseConnectionPool())
            p.addHeader(CONNECTION_CLOSE_HEADER);

        StringWriter stringWriter = new StringWriter();
        // write request
        protocol.writeRequest(stringWriter, method.getName(), arguments);
        String methodInvocation = stringWriter.toString();
        StringEntity entity = new StringEntity(methodInvocation, protocol.getContentType(), "UTF-8");
        p.setEntity(entity);
        final RpcOverHttpRequestContextImpl requestContext = new RpcOverHttpRequestContextImpl(p, method.getDeclaringClass().getName(),
                method.getName(), methodInvocation);

        // fire preInvoke event
        if (eventHandler != null)
            eventHandler.preInvoke(requestContext);

        HttpResponse response = null;
        Object result = null;
        long startTimeMillis = System.currentTimeMillis();
        try
        {
            // TODO : Kfir, clean up this logging hack

            try
            {
                response = httpClient.execute(p);
            }
            catch (Exception e)
            {
                p.abort();

                long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;

                throw new RpcTransportException(format("Exception while communicating with server [endpoint=%s,timeSpentMillis=%d,poolStats=%s]",
                        getEndPointDescription(method), timeSpentMillis, getConnectionPoolStateString()), e);
            }

            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK)
            {
                safeConsumeResponseEntity(responseEntity);

                p.abort();

                long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;

                throw new RpcTransportException(format("Unexpected server HTTP status code [endpoint=%s,httpStatusLine='%s',timeSpentMillis=%d,poolStats=%s]",
                        getEndPointDescription(method), response.getStatusLine().toString(), timeSpentMillis, getConnectionPoolStateString()), response.getStatusLine().getStatusCode());
            }
            // read the response

            long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;
            if (responseEntity == null)
            {
                p.abort();
                throw new RpcTransportException(format("Empty HTTP response [endpoint=%s,httpStatusLine='%s',timeSpentMillis=%d,poolStats=%s]",
                        getEndPointDescription(method), response.getStatusLine().toString(), timeSpentMillis, getConnectionPoolStateString()), response.getStatusLine().getStatusCode());
            }

            InputStream s = responseEntity.getContent();

            try
            {
                result = protocol.readResponse(method.getGenericReturnType(), method.getExceptionTypes(), new InputStreamReader(s, Charset.forName("UTF-8")));

                // fire postInvoke event
                if (eventHandler != null)
                    eventHandler.postInvoke(requestContext, new RpcOverHttpResponseContextImpl(result, response, timeSpentMillis));
            }
            catch (Exception e)
            {
                p.abort();
                throw e;
            }
            finally
            {
                safeConsumeResponseEntity(responseEntity);
            }
        }
        catch (Exception e)
        {
            long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;
            // fire postInvoke event
            if (eventHandler != null)
                eventHandler.postInvoke(requestContext, new RpcOverHttpResponseContextImpl(e, response, timeSpentMillis));

            throw e;
        }

        return result;
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

    private String getEndPointDescription(Method method)
    {
        return format("{url='%s',serviceInterfaceClass='%s',method='%s'}",
                serviceUrl,
                method.getDeclaringClass().getName(),
                method.getName());
    }

    String getConnectionPoolStateString()
    {
        return "SingleClientConnManager";
    }
}
