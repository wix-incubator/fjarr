package com.wixpress.framework.rpc.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author alexeyr
 * @since 6/30/11 2:08 PM
 */

public interface RpcRequestLifecycleEventHandler
{
    void handleRecievedRequest(HttpServletRequest request, HttpServletResponse response);

    void handleRequestParsed(HttpServletRequest request, HttpServletResponse response, Class<?> serviceInterface,  RpcRequest rpcRequest);

    /**
     * Event is fired when the method is invoked, even if the invocation failed. All implementations should check whether the invocation.error != null
     * to see whether the resolving succeeded
     * @param request
     * @param response
     * @param rpcRequest
     * @param invocation
     */
    void handleRpcInvocationMethodResolved(HttpServletRequest request, HttpServletResponse response, RpcRequest rpcRequest, RpcInvocation invocation);

    void handleRpcInvocationMethodInvoked(HttpServletRequest request, HttpServletResponse response, RpcRequest rpcRequest, RpcInvocation invocation);

    void handleRpcResponseWriting(HttpServletRequest request, HttpServletResponse response, RpcRequest rpcRequest);

    void handleRpcResponseWritten(HttpServletRequest request, HttpServletResponse response, RpcRequest rpcRequest);

    void handleRpcResponseWriteError(HttpServletRequest request, HttpServletResponse response, Exception exception, String requestBody, RpcRequestStatistics statistics);

}
