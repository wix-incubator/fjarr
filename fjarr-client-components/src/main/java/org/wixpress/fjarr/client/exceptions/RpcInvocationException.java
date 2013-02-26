package org.wixpress.fjarr.client.exceptions;

/**
 * @author alexeyr
 * @since 6/30/11 6:37 PM
 */

/**
 * This exception is thrown whenever a runtime exception have happened on the server side.
 * This exception may include some information about the server side exception.
 */
public class RpcInvocationException extends RuntimeException
{
    private Exception serverException = null;
    private int errorCode;

    public RpcInvocationException(String message)
    {
        super(message);
    }

    public RpcInvocationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RpcInvocationException(String message, int errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }


    public RpcInvocationException(Exception serverException)
    {
        super(serverException.getMessage());
        this.serverException = serverException;
    }

    public boolean hasServerException()
    {
        return serverException != null;
    }

    public Exception getServerException()
    {
        return serverException;
    }

    public int getErrorCode()
    {
        return errorCode;
    }
}
