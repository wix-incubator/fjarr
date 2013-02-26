package org.wixpress.fjarr.example;

/**
 * @author alexeyr
 * @since Oct 5, 2010 11:02:09 AM
 *        IT exception
 */

public class DataStructServiceRuntimeException extends RuntimeException
{
    public DataStructServiceRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DataStructServiceRuntimeException(String message)
    {
        super(message);
    }
}
