package com.wixpress.fjarr.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author alexeyr
 * @since Oct 5, 2010 11:02:09 AM
 *        it exception
 */


public class DataStructServiceComplexException extends Exception
{
    int i = -1;

    @JsonCreator
    public DataStructServiceComplexException(@JsonProperty("message") String message, @JsonProperty("i") int i)
    {
        super(message);
        this.i = i;
    }

    public int getI()
    {
        return i;
    }
}


