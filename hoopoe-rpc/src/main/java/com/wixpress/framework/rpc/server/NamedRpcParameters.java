package com.wixpress.framework.rpc.server;

import java.util.Map;

/**
 * @author AlexeyR
 * @since 6/12/11 11:30 AM
 */

public class NamedRpcParameters implements RpcParameters<Map<String, Object>>
{

    private final Map<String, Object> params;

    public NamedRpcParameters(Map<String, Object> params)
    {
        this.params = params;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        return params;
    }
}
