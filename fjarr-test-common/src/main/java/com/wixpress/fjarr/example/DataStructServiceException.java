package com.wixpress.fjarr.example;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author alexeyr
 * @since Oct 5, 2010 11:02:09 AM
 *        IT exception
 */


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DataStructServiceException extends Exception
{
    public DataStructServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DataStructServiceException(String message)
    {
        super(message);
    }
}


