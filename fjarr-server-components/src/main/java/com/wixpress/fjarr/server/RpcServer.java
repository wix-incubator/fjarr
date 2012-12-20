package com.wixpress.fjarr.server;


import com.wixpress.fjarr.server.exceptions.*;
import com.wixpress.fjarr.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author alexeyr
 * @since 6/2/11 1:59 PM
 */

public class RpcServer
{
    private final RpcProtocol protocol;
    private final Object serviceImpl;
    private final Class<?> serviceInterface;
    RpcRequestLifecycleEventHandler lifecycleEventHandler;

    private Logger log = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer(RpcProtocol protocol, Object serviceImpl, Class<?> serviceInterface)
    {
        this.protocol = protocol;
        this.serviceImpl = serviceImpl;
        this.serviceInterface = serviceInterface;
    }

    public RpcServer(RpcProtocol protocol, Object serviceImpl, Class<?> serviceInterface, RpcRequestLifecycleEventHandler lifecycleEventHandler)
    {
        this.protocol = protocol;
        this.serviceImpl = serviceImpl;
        this.serviceInterface = serviceInterface;
        this.lifecycleEventHandler = lifecycleEventHandler;
    }

    public void handleRequest(RpcRequest request, RpcResponse response)
            throws UnsupportedContentTypeException, BadRequestException, HttpMethodNotAllowedException, GenericRpcException
    {
        RpcRequestStatistics stats = new RpcRequestStatistics(System.currentTimeMillis());

        try
        {
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRecievedRequest(request, response);

            ParsedRpcRequest parsedRequest = protocol.parseRequest(request);
            stats.setRequestParsingFinishedTimestamp(System.currentTimeMillis());
//            rpcRequest.setStatistics(stats);
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRequestParsed(parsedRequest, response, serviceInterface);

            for (RpcInvocation invocation : parsedRequest.getInvocations())
            {
                if (!invocation.isError())
                {
                    invokeMethod(parsedRequest, response, invocation);
                }
            }
            stats.setRequestProcessingFinishedTimestamp(System.currentTimeMillis());
            protocol.writeResponse(response, parsedRequest);

            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWritten(parsedRequest, response);
        }
        catch (HttpMethodNotAllowedException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(request, response, e, stats);
            throw e;
        }
        catch (UnsupportedContentTypeException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(request, response, e, stats);

            throw e;
        }
        catch (BadRequestException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            log.error(e.getMessage(), e);
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(request, response, e, stats);
            throw e;
        }
        catch (Exception e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            if (lifecycleEventHandler != null)
                            lifecycleEventHandler.handleRpcResponseWriteError(request, response, e, stats);
            throw new GenericRpcException(e.getMessage(),e);
        }
    }


    void invokeMethod(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation)
    {
        try
        {
            List<Method> methods = ReflectionUtils.findMethods(serviceInterface, invocation.getMethodName());
            if (methods == null || methods.size() == 0)
            {
                invocation.setError(new MethodNotFoundException("Method [%s] was not found", invocation.getMethodName()));
                // hook for handling resolving error
                if (lifecycleEventHandler != null)
                    lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, invocation);
                return;
            }
            protocol.resolveMethod(methods, invocation, request);
            // hook for handling resolving success
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, invocation);

        }
        catch (Exception e)
        {
            invocation.setError(e);
            // hook for handling resolving error
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, invocation);
        }
        if (!invocation.isError())
        {
            try
            {
                Object result = invocation.getResolvedMethod().invoke(serviceImpl, invocation.getResolvedParameters());
                invocation.setInvocationResult(result);
                // hook for handling invocation success
                if (lifecycleEventHandler != null)
                    lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, invocation);
            }
            catch (Exception e)
            {
                if (e instanceof InvocationTargetException)
                {
                    InvocationTargetException ite = (InvocationTargetException) e;
                    if (ite.getTargetException() instanceof Exception)
                    {
                        invocation.setError((Exception) ite.getTargetException());

                    }
                    else
                    {
                        // target exception is a non-Exception Throwable, something horrible must've happened
                        invocation.setError(new RuntimeException("Fatal error encountered in RPC server", ite.getTargetException()));
                    }

                }
                else
                {
                    invocation.setError(e);
                }

                // hook for handling invocation error
                if (lifecycleEventHandler != null)
                    lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, invocation);
            }

        }

    }

    public RpcProtocol getProtocol()
    {
        return protocol;
    }

    public Object getServiceImpl()
    {
        return serviceImpl;
    }

    public Class<?> getServiceInterface()
    {
        return serviceInterface;
    }


}
