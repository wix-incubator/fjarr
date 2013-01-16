package com.wixpress.fjarr.server;


import com.wixpress.fjarr.server.exceptions.*;
import com.wixpress.fjarr.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    List<RpcRequestLifecycleEventHandler> lifecycleEventHandlers = new ArrayList<RpcRequestLifecycleEventHandler>();

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
        this.lifecycleEventHandlers.add(lifecycleEventHandler);
    }

    public void handleRequest(final RpcRequest request, final RpcResponse response)
            throws UnsupportedContentTypeException, BadRequestException, HttpMethodNotAllowedException, GenericRpcException
    {
        final RpcRequestStatistics stats = new RpcRequestStatistics(System.currentTimeMillis());

        try
        {

            // fire Request Received event
            // could be written as onEvent(lifecycleEventHandler->  lifecycleEventHandler.handleReceivedRequest(request, response))
            // if Java would finally support lamda expressions

            LifecycleEventFlow flow = onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleReceivedRequest(request, response);
                }
            });
            // if wasn't told to stop - proceed
            if (!(flow instanceof LifecycleEventFlow.Proceed))
                return;


            final ParsedRpcRequest parsedRequest = protocol.parseRequest(request);
            stats.setRequestParsingFinishedTimestamp(System.currentTimeMillis());


            // fire Request Parsed event
            flow = onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRequestParsed(parsedRequest, response, serviceInterface);
                }
            });
            // if wasn't told to stop - proceed
            if (!(flow instanceof LifecycleEventFlow.Proceed))
                return;


            for (RpcInvocation invocation : parsedRequest.getInvocations())
            {
                if (!invocation.isError())
                {
                    invokeMethod(parsedRequest, response, invocation);
                }
            }
            stats.setRequestProcessingFinishedTimestamp(System.currentTimeMillis());
            flow = onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcResponseWriting(parsedRequest, response);
                }
            });
            // if wasn't told to stop - proceed
            if (!(flow instanceof LifecycleEventFlow.Proceed))
                return;
            protocol.writeResponse(response, parsedRequest);


            // fire Request Parsed event
            onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcResponseWritten(parsedRequest, response);
                }
            });
        }
        catch (final HttpMethodNotAllowedException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcServerError(request, response, e, stats);
                }
            });

            throw e;
        }
        catch (final UnsupportedContentTypeException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcServerError(request, response, e, stats);
                }
            });

            throw e;
        }
        catch (final BadRequestException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            log.error(e.getMessage(), e);
            onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcServerError(request, response, e, stats);
                }
            });
            throw e;
        }
        catch (final Exception e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcServerError(request, response, e, stats);
                }
            });
            throw new GenericRpcException(e.getMessage(), e);
        }
    }


    void invokeMethod(final ParsedRpcRequest request, final RpcResponse response, final RpcInvocation invocation)
    {
        try
        {
            // fire Request Parsed event
            LifecycleEventFlow flow = onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcInvocationMethodResolving(request, response, invocation);
                }
            });
            // if wasn't told to stop - proceed
            if (!(flow instanceof LifecycleEventFlow.Proceed))
                return;


            List<Method> methods = ReflectionUtils.findMethods(serviceInterface, invocation.getMethodName());
            if (methods == null || methods.size() == 0)
            {
                invocation.setError(new MethodNotFoundException("Method [%s] was not found", invocation.getMethodName()));
                // hook for handling resolving error
                flow = onEvent(new EventExecutor()
                {
                    @Override
                    public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                    {
                        return lifecycleEventHandler.handleRpcInvocationMethodResolvingError(request, response, invocation);
                    }
                });
                return;
            }
            protocol.resolveMethod(methods, invocation, request);
            // hook for handling resolving success
        }
        catch (Exception e)
        {
            invocation.setError(e);
            // hook for handling resolving error
            LifecycleEventFlow flow = onEvent(new EventExecutor()
            {
                @Override
                public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                {
                    return lifecycleEventHandler.handleRpcInvocationMethodResolvingError(request, response, invocation);
                }
            });
            // if wasn't told to stop - proceed
            if (!(flow instanceof LifecycleEventFlow.Proceed))
                return;
        }


        if (!invocation.isError())
        {
            try
            {
                // Resolving success - fire event
                LifecycleEventFlow flow = onEvent(new EventExecutor()
                {
                    @Override
                    public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                    {
                        return lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, invocation);
                    }
                });
                // if wasn't told to stop - proceed
                if (!(flow instanceof LifecycleEventFlow.Proceed))
                    return;


                Object result = invocation.getResolvedMethod().invoke(serviceImpl, invocation.getResolvedParameters());
                invocation.setInvocationResult(result);
                // hook for handling invocation success
                onEvent(new EventExecutor()
                {
                    @Override
                    public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                    {
                        return lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, invocation);
                    }
                });
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
                onEvent(new EventExecutor()
                {
                    @Override
                    public LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler)
                    {
                        return lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, invocation);
                    }
                });
            }

        }

    }

    /*  Event Handling  */

    private static interface EventExecutor
    {
        LifecycleEventFlow executeHandler(RpcRequestLifecycleEventHandler lifecycleEventHandler);
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


    public void addLifecycleEventHandler(RpcRequestLifecycleEventHandler handler)
    {
        lifecycleEventHandlers.add(handler);
    }

    public void removeLifecycleEventHandler(RpcRequestLifecycleEventHandler handler)
    {
        lifecycleEventHandlers.remove(handler);
    }

    public void clearLifecycleEventHandler()
    {
        lifecycleEventHandlers.clear();
    }


}
