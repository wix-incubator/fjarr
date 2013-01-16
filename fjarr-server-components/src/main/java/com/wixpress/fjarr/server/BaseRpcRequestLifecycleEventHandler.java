package com.wixpress.fjarr.server;

/**
 * @author alex
 * @since 1/16/13 1:27 PM
 *
 * Convenience base class for LifecycleEventHandler implementations, so that the inheritor classes would only need to override
 * The required events. Returns 'proceed' to all events
 */
public class BaseRpcRequestLifecycleEventHandler implements RpcRequestLifecycleEventHandler
{
    @Override
    public LifecycleEventFlow handleReceivedRequest(RpcRequest request, RpcResponse response)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRequestParsed(ParsedRpcRequest request, RpcResponse response, Class<?> serviceInterface)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcInvocationMethodResolving(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcInvocationMethodResolved(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcInvocationMethodResolvingError(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcInvocationMethodInvoked(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcResponseWriting(ParsedRpcRequest request, RpcResponse response)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcResponseWritten(ParsedRpcRequest request, RpcResponse response)
    {
        return LifecycleEventFlow.proceed();
    }

    @Override
    public LifecycleEventFlow handleRpcServerError(RpcRequest request, RpcResponse response, Exception exception, RpcRequestStatistics statistics)
    {
        return LifecycleEventFlow.proceed();
    }
}
