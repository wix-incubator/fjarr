package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.SettableFuture;

/**
 * @author alex
 * @since 2/12/13 4:32 PM
 */

public class RequestResponse
{
    public RpcInvocation invocation;
    public SettableFuture<RpcInvocationResponse> responseFuture;

    public RequestResponse(RpcInvocation invocation, SettableFuture<RpcInvocationResponse> responseFuture)
    {
        this.invocation = invocation;
        this.responseFuture = responseFuture;
    }
}
