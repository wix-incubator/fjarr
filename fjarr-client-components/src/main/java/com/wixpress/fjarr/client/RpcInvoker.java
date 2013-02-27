package com.wixpress.fjarr.client;

import java.io.IOException;

/**
 * @author AlexeyR
 * @since 12/6/12 5:49 PM
 */

public interface RpcInvoker
{
    RpcInvocationResponse invoke(RpcInvocation invocation) throws IOException;

}
