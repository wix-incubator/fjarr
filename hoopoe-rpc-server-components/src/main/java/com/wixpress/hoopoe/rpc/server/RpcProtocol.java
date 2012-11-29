package com.wixpress.hoopoe.rpc.server;

import com.wixpress.hoopoe.rpc.server.exceptions.BadRequestException;
import com.wixpress.hoopoe.rpc.server.exceptions.HttpMethodNotAllowedException;
import com.wixpress.hoopoe.rpc.server.exceptions.UnsupportedContentTypeException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author alexeyr
 * @since 6/2/11 2:01 PM
 */

public interface RpcProtocol
{

    /**
     * this resolvedMethod is invocated by the RpcOverHttpServer, an is supposed to do initial request parsing.
     * After this resolvedMethod completes the RpcMethodCalls should have methodName, and parameters (unmarshalled) initialized.
     *
     * @param request
     * @return RpcProcessedRequest instance which holds references to all RpcInvocation objects
     * @throws java.io.IOException                   thrown id the input stream cannot be read
     * @throws HttpMethodNotAllowedException thrown if the HTTP resolvedMethod (POST, GET, PUT) is not suppoted by the protocol
     * @throws UnsupportedContentTypeException
     *                                       thrown if the request's content-type is not supported by the protocol
     * @throws BadRequestException           thrown if the whole request cannot be parsed
     *                                       (for example if the request is xml - this exception should be thrown if the xml is not well-formed)
     */
    ParsedRpcRequest parseRequest(RpcRequest request) throws IOException, HttpMethodNotAllowedException, UnsupportedContentTypeException, BadRequestException;

    /**
     * resolved called resolvedMethod from list of Method objects
     *
     * @param methods
     * @param invocation
     */
    void resolveMethod(List<Method> methods, RpcInvocation invocation, ParsedRpcRequest request);

    /**
     * Marshalles the responces accumulated in the RpcRequest to the response
     *
     * @param response
     * @param request
     * @throws IOException if the response cannot be written
     */
    void writeResponse(RpcResponse response, ParsedRpcRequest request) throws IOException;
}
