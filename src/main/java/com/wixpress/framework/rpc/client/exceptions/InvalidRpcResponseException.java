package com.wixpress.framework.rpc.client.exceptions;

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
}
