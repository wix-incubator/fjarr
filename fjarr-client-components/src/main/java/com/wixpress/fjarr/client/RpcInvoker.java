package com.wixpress.fjarr.client;

/**
 * @author AlexeyR
 * @since 12/6/12 5:49 PM
 */

public interface RpcInvoker
{
    String urlEncode(String name);

    RpcInvocationResponse invoke(RpcInvocation invocation);

    void abort(RpcInvocation invocation);
}
