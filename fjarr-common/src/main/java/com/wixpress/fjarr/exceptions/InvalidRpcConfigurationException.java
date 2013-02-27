package com.wixpress.fjarr.exceptions;

/**
 * @author alex
 * @since 11/30/11 11:47 AM
 */


public class InvalidRpcConfigurationException extends RuntimeException
{
    public InvalidRpcConfigurationException(String message, Throwable cause, Object... args)
    {
        super(String.format(message, args), cause);
    }

    public InvalidRpcConfigurationException(String message, Object... args)
    {
        super(String.format(message, args));
    }
}
