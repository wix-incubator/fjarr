package org.wixpress.fjarr.server.exceptions;

/**
 * @author AlexeyR
 * @since 6/12/11 9:02 AM
 */

/**
 * This exception should only be thrown at a rpc service over-http-exporter, when it can't be parsed
 * (for example a not well-formed xml was sent to the server)
 * This exception MUST generate a responce with 400 Bad Request
 */
public class GenericRpcException extends Exception
{
    public GenericRpcException(String message)
    {
        super(message);
    }

    public GenericRpcException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
