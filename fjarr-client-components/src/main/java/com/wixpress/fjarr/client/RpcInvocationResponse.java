package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;
import com.wixpress.fjarr.util.ReadOnlyMultiMap;

/**
 * @author AlexeyR
 * @since 12/6/12 6:35 PM
 */

public class RpcInvocationResponse
{
    private final int statusCode;
    private final String statusDescription;
    private final String body;
    private final MultiMap<String, String> headers = new MultiMap<String, String>();

    public RpcInvocationResponse(int statusCode, String statusDescription, String body, MultiMap<String, String> headers) {
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
        this.body = body;
        this.headers.putAll(headers);
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String getStatusDescription()
    {
        return statusDescription;
    }

    public String getBody()
    {
        return body;
    }

    public ReadOnlyMultiMap<String,String> getAllHeaders()
    {
        return headers;
    }
}
