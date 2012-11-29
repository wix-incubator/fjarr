package com.wixpress.hoopoe.rpc.server;

/**
 * @author alexeyr
 * @since 6/30/11 2:08 PM
 */

public interface RpcRequestLifecycleEventHandler
{
    void handleRecievedRequest(RpcRequest request, RpcResponse response);

    void handleRequestParsed(ParsedRpcRequest request, RpcResponse response, Class<?> serviceInterface);

    /**
     * Event is fired when the method is invoked, even if the invocation failed. All implementations should check whether the invocation.error != null
     * to see whether the resolving succeeded
     *
     * @param request
     * @param response
     * @param invocation
     */
    void handleRpcInvocationMethodResolved(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation);

    void handleRpcInvocationMethodInvoked(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation);

    void handleRpcResponseWriting(ParsedRpcRequest request, RpcResponse response);

    void handleRpcResponseWritten(ParsedRpcRequest request, RpcResponse response);

    void handleRpcResponseWriteError(RpcRequest request, RpcResponse response, Exception exception, RpcRequestStatistics statistics);

}
