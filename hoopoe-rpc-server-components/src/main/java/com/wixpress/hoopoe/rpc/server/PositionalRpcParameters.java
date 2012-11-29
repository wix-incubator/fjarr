package com.wixpress.hoopoe.rpc.server;

/**
 * @author AlexeyR
 * @since 6/12/11 11:28 AM
 */

public class PositionalRpcParameters implements RpcParameters<Object[]>
{

    Object[] params;

    public PositionalRpcParameters(Object[] params)
    {
        this.params = params;
    }

    @Override
    public Object[] getParameters()
    {
        return params;
    }
}
