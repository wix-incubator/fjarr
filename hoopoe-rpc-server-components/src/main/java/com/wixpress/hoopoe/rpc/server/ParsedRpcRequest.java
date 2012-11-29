package com.wixpress.hoopoe.rpc.server;

import java.util.List;

/**
 * @author AlexeyR
 * @since 11/25/12 9:09 AM
 */

/**
 * This class represents an abstraction over a Rpc request sent to a server, that was parsed by a protocol.
 * In addition to inherited members, it contains a list of Rpc Invocations that the server should execute.
 * (A list is needed to accommodate batch requests)
 */
public class ParsedRpcRequest
{
    protected final List<RpcInvocation> invocations;

    protected RpcRequest baseRequest;
    public ParsedRpcRequest(RpcRequest baseRequest, List<RpcInvocation> invocations)
    {
        this.baseRequest = baseRequest;
        this.invocations = invocations;
    }

    public static ParsedRpcRequest fromRpcRequest(RpcRequest request, List<RpcInvocation> invocations)
    {
        return new ParsedRpcRequest(request, invocations);
    }

    public List<RpcInvocation> getInvocations()
    {
        return invocations;
    }
}
