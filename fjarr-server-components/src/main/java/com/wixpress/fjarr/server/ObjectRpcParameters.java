package com.wixpress.fjarr.server;

/**
 * @author alex
 * @since 1/7/13 1:32 PM
 */

public class ObjectRpcParameters implements RpcParameters<Object>
{


    private Object obj;

    public ObjectRpcParameters(Object obj)
    {
        this.obj = obj;
    }

    @Override
    public Object getParameters()
    {
        return obj;
    }
}
