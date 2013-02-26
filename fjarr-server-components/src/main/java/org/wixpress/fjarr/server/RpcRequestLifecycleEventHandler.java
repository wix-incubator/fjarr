package org.wixpress.fjarr.server;

/**
 * @author alexeyr
 * @since 6/30/11 2:08 PM
 */

public interface RpcRequestLifecycleEventHandler
{
    /**
     * Request was recieved by the server
     */
    LifecycleEventFlow handleReceivedRequest(RpcRequest request, RpcResponse response);

    /**
     * Request have been parsed sucessfully
     */
    LifecycleEventFlow handleRequestParsed(ParsedRpcRequest request, RpcResponse response, Class<?> serviceInterface);


    /**
     * Event is fired for each invocation, just before the method is resolved. If an implementation needs to override
     * method resolving, or hijack method execution (for example for implementing protocol extension methods) -
     * this event is the place for it.
     */
    LifecycleEventFlow handleRpcInvocationMethodResolving(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation);

    /**
     * Event is fired just before the method is invoked. If an implementation needs to perform a last minute activity,
     * such as parameter validation - this event is the place for it.
     */
    LifecycleEventFlow handleRpcInvocationMethodResolved(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation);


    /**
     * Method resolving have failed.
     */
    LifecycleEventFlow handleRpcInvocationMethodResolvingError(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation);

    /**
     * Rpc method was invoked
     */
    LifecycleEventFlow handleRpcInvocationMethodInvoked(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation);

    /**
     * The response is about to be written to the output-stream. If an implementation needs to override the output,
     * this event is the last place to do it
     */
    LifecycleEventFlow handleRpcResponseWriting(ParsedRpcRequest request, RpcResponse response);

    /**
     * The response was written to the output-stream
     */
    LifecycleEventFlow handleRpcResponseWritten(ParsedRpcRequest request, RpcResponse response);

    /**
     * Generic server error have occured. One example of such error is when the server tries to write to an output stream
     * that was already closed by the client.
     */
    LifecycleEventFlow handleRpcServerError(RpcRequest request, RpcResponse response, Exception exception, RpcRequestStatistics statistics);

}
