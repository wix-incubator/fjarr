package com.wixpress.framework.rpc.client.exceptions;

/**
 * @author alex
 * @since 11/1/11 11:25 AM
 */

/**
 * This exception is thrown whenever the communication with server encountered and error,
 * or the server responded with HTTP status code other than 200
 */
public class RpcTransportException extends RuntimeException
{
    private int statusCode;

    public RpcTransportException(String s,  Throwable cause)
    {
        super(s, cause);
    }

    public RpcTransportException(String s, int statusCode)
    {
        super(s);
        this.statusCode = statusCode;

    }

    public RpcTransportException(String s, int statusCode, Throwable cause)
    {
        super(s, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return statusCode;
    }
}
