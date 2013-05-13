package com.wixpress.fjarr.server;


import com.wixpress.fjarr.server.exceptions.*;
import com.wixpress.fjarr.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final List<RpcRequestLifecycleEventHandler> lifecycleEventHandlers;

    private Logger log = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer(RpcProtocol protocol, Object serviceImpl, Class<?> serviceInterface, RpcRequestLifecycleEventHandler... lifecycleEventHandlers)
    {
        this(protocol, serviceImpl, serviceInterface, Arrays.asList(lifecycleEventHandlers));
    }

    public RpcServer(RpcProtocol protocol, Object serviceImpl, Class<?> serviceInterface, List<RpcRequestLifecycleEventHandler> lifecycleEventHandlers) {
        this.protocol = protocol;
        this.serviceImpl = serviceImpl;
        this.serviceInterface = serviceInterface;
        this.lifecycleEventHandlers = new ArrayList<RpcRequestLifecycleEventHandler>(lifecycleEventHandlers);
    }

    public void handleRequest(final RpcRequest request, final RpcResponse response)
            throws UnsupportedContentTypeException, BadRequestException, HttpMethodNotAllowedException, GenericRpcException
    {
        final RpcRequestStatistics stats = new RpcRequestStatistics(System.currentTimeMillis());

        try
        {

            // fire Request Received event
            if (!fireReceivedRequest(request, response).isProceed()) return;

            final ParsedRpcRequest parsedRequest = protocol.parseRequest(request);
            stats.setRequestParsingFinishedTimestamp(System.currentTimeMillis());

            if (!fireRequestParsed(parsedRequest, response).isProceed()) return;


            for (RpcInvocation invocation : parsedRequest.getInvocations())
            {
                if (!invocation.isError())
                {
                    invokeMethod(parsedRequest, response, invocation);
                }
            }
            stats.setRequestProcessingFinishedTimestamp(System.currentTimeMillis());

            if (!fireRpcResponseWriting(parsedRequest, response).isProceed()) return;

            protocol.writeResponse(response, parsedRequest);

            fireRpcResponseWritten(parsedRequest, response);
        }
        catch (final HttpMethodNotAllowedException e)
        {
            fireServerErrorEvent(request, response, stats, e);
            throw e;
        }
        catch (final UnsupportedContentTypeException e)
        {
            fireServerErrorEvent(request, response, stats, e);
            throw e;
        }
        catch (final BadRequestException e)
        {
            log.error(e.getMessage(), e);
            fireServerErrorEvent(request, response, stats, e);
            throw e;

        }
        catch (final Exception e)
        {
            fireServerErrorEvent(request, response, stats, e);
            throw new GenericRpcException(e.getMessage(), e);
        }
    }


    void invokeMethod(final ParsedRpcRequest request, final RpcResponse response, final RpcInvocation invocation)
    {
        try
        {

            if (!fireRpcInvocationMethodResolving(request, response, invocation).isProceed()) return;

            List<Method> methods = ReflectionUtils.findMethods(serviceInterface, invocation.getMethodName());
            if (methods == null || methods.size() == 0)
            {
                fireRpcInvocationMethodResolvingError(request, response, invocation,
                        new MethodNotFoundException("Method [%s] was not found", invocation.getMethodName()));
                return;
            }
            protocol.resolveMethod(methods, invocation, request);
            // hook for handling resolving success
        }
        catch (Exception e)
        {
            if (!fireRpcInvocationMethodResolvingError(request, response, invocation, e).isProceed()) return;
        }


        if (!invocation.isError())
        {
            try
            {
                // Resolving success - fire event
                if (!fireRpcInvocationMethodResolved(request, response, invocation).isProceed()) return;

                Object result = invocation.getResolvedMethod().invoke(serviceImpl, invocation.getResolvedParameters());
                invocation.setInvocationResult(result);


                // hook for handling invocation success
                fireRpcInvocationMethodInvoked(request, response, invocation);
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
                fireRpcInvocationMethodInvoked(request, response, invocation);
            }

        }

    }


    /*  Event Handling  */

    private static interface EventExecutor
    {
        LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler);
    }


    private LifecycleEventFlow fireReceivedRequest(final RpcRequest request, final RpcResponse response)
    {
        // could be written as onEvent(lifecycleEventHandler->  lifecycleEventHandler.handleReceivedRequest(request, response))
        // if Java would finally support lambda expressions
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleReceivedRequest(request, response);
            }
        });
    }

    private LifecycleEventFlow fireRequestParsed(final ParsedRpcRequest parsedRequest, final RpcResponse response)
    {
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRequestParsed(parsedRequest, response, serviceInterface);
            }
        });
    }


    private LifecycleEventFlow fireRpcResponseWriting(final ParsedRpcRequest parsedRequest, final RpcResponse response)
    {
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcResponseWriting(parsedRequest, response);
            }
        });
    }

    private LifecycleEventFlow fireRpcResponseWritten(final ParsedRpcRequest parsedRequest, final RpcResponse response)
    {
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcResponseWritten(parsedRequest, response);
            }
        });
    }

    private LifecycleEventFlow fireServerErrorEvent(final RpcRequest request, final RpcResponse response, final RpcRequestStatistics stats, final Exception e)
    {
        stats.setRequestErrorTimestamp(System.currentTimeMillis());
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcServerError(request, response, e, stats);
            }
        });
    }

    private LifecycleEventFlow fireRpcInvocationMethodResolving(final ParsedRpcRequest request, final RpcResponse response, final RpcInvocation invocation)
    {
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcInvocationMethodResolving(request, response, invocation);
            }
        });
    }

    private LifecycleEventFlow fireRpcInvocationMethodResolved(final ParsedRpcRequest request, final RpcResponse response, final RpcInvocation invocation)
    {
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, invocation);
            }
        });
    }

    private void fireRpcInvocationMethodInvoked(final ParsedRpcRequest request, final RpcResponse response, final RpcInvocation invocation)
    {
        onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, invocation);
            }
        });
    }

    private LifecycleEventFlow fireRpcInvocationMethodResolvingError(final ParsedRpcRequest request, final RpcResponse response, final RpcInvocation invocation, Exception e)
    {
        invocation.setError(e);
        // hook for handling resolving error
        return onEvent(new EventExecutor()
        {
            @Override
            public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
            {
                return lifecycleEventHandler.handleRpcInvocationMethodResolvingError(request, response, invocation);
            }
        });
    }



    private LifecycleEventFlow onEvent(EventExecutor executor)
    {
        for (RpcRequestLifecycleEventHandler lifecycleEventHandler : lifecycleEventHandlers)
        {
            final LifecycleEventFlow eventFlow = executor.executeHandler(lifecycleEventHandler);

            if (eventFlow instanceof LifecycleEventFlow.Proceed)
            {
                // proceed
            }
            else if (eventFlow instanceof LifecycleEventFlow.Throw)
            {
                ((LifecycleEventFlow.Throw) eventFlow).raise();
            }
            else if (eventFlow instanceof LifecycleEventFlow.StopEvent)
            {
                break;
            }
            else if (eventFlow instanceof LifecycleEventFlow.StopRequest)
            {
                return eventFlow;
            }
        }

        return LifecycleEventFlow.proceed();

    }

}
