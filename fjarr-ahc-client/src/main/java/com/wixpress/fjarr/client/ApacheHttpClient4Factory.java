package com.wixpress.fjarr.client;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * @author alex
 * @since 12/25/12 5:33 PM
 */

public class ApacheHttpClient4Factory implements HttpClientFactory
{
    private HttpClientConfig config;

    public ApacheHttpClient4Factory(HttpClientConfig config)
    {
        this.config = config;
    }

    @Override
    public HttpClient createHttpClient()
    {
        DefaultHttpClient hc = new DefaultHttpClient(buildConnectionManager());

        HttpParams params = hc.getParams();
        HttpConnectionParams.setConnectionTimeout(params, config.getConnectionTimeoutMillis());
        HttpConnectionParams.setSoTimeout(params, config.getSocketTimeoutMillis());
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
        return hc;
    }

    @Override
    public boolean useConnectionPool()
    {
        return false;
    }

    private ClientConnectionManager buildConnectionManager()
    {
        // init the http client
        return new SingleClientConnManager();
    }
}
