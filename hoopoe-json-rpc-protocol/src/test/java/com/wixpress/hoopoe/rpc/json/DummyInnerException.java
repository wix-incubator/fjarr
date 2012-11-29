package com.wixpress.hoopoe.rpc.json;

/**
* @author shaiyallin
* @since 1/9/12
*/
public class DummyInnerException extends Exception
{
    int i;

    public DummyInnerException(String message)
    {
        super(message);
    }

    public DummyInnerException(String message, int i)
    {
        super(message);
        this.i = i;
    }
}
