package com.wixpress.fjarr.server.exceptions;

/**
 * @author AlexeyR
 * @author DanielS
 * @since 6/12/11 3:49 PM
 */

public class MethodNotFoundException extends RuntimeException
{
    public MethodNotFoundException()
    {
        ;
    }

    public MethodNotFoundException(String message)
    {
        super(message);
    }

    public MethodNotFoundException(String message, Object... params)
    {
        super(String.format(message, params));
    }

    public MethodNotFoundException(String message, Throwable cause, Object ... params)
    {
        super(String.format(message, params), cause);
    }
}
