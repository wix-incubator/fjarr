package com.wixpress.fjarr.client.exceptions;

/**
 * @author alexeyr
 * @since 6/30/11 6:36 PM
 */

public class InvalidRpcResponseException extends RpcInvocationException
{
    public InvalidRpcResponseException(String message)
    {
        super(message);
    }

    public InvalidRpcResponseException(String message,Throwable cause)
    {
        super(message,cause);
    }

}
