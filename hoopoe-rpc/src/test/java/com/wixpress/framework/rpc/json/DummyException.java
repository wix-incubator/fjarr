package com.wixpress.framework.rpc.json;

/**
* @author shaiyallin
* @since 1/9/12
*/
public class DummyException extends Exception
{
    public DummyException(String message)
    {
        super(message);
    }

    public DummyException(String message, Exception cause)
    {
        super(message, cause);

    }



}
