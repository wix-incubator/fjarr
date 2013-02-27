package com.wixpress.fjarr.client;

import org.apache.http.client.HttpClient;

/**
 * @author alex
 * @since 12/25/12 5:32 PM
 */

public interface HttpClientFactory
{
    HttpClient createHttpClient();

    boolean useConnectionPool();
}
