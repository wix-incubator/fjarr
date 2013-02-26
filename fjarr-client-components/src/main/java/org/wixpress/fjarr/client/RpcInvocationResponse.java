package org.wixpress.fjarr.client;

import org.wixpress.fjarr.util.MultiMap;

/**
 * @author AlexeyR
 * @since 12/6/12 6:35 PM
 */

public class RpcInvocationResponse
{
    private int statusCode;
    private String statusDescription;
    private String body;
    private MultiMap<String, String> headers;

    public RpcInvocationResponse(int statusCode, String statusDescription, String body, MultiMap<String, String> headers) {
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
        this.body = body;
        this.headers = headers;
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

    public MultiMap<String,String> getAllHeaders()
    {
        return headers;
    }
}
