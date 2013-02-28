package com.wixpress.fjarr.client;

import com.wixpress.fjarr.client.exceptions.RpcClientInitializationException;
import com.wixpress.fjarr.client.exceptions.RpcInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

/**
 * @author shaiyallin
 * @author AlexeyR
 * @since 1/3/12
 */

public class RpcClientProxy<T> implements InvocationHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientProxy.class);

    private final Class<T> serviceInterface;
    private final String serviceUrl;

    private final RpcClient client;


    private final InvocationExecutor[] invocationExecutors = new InvocationExecutor[]{
            new ToStringExecutor(), new RemoteExecutor()
    };


    private RpcClientProxy(Class<T> serviceInterface, String serviceUrl,
                           RpcClientProtocol protocol, RpcInvoker invoker,
                           RpcClientEventHandler eventHandler)
    {

        if (!serviceInterface.isInterface())
        {
            throw new RpcClientInitializationException(
                    format("Class %s is not an interface and cannot be used to create an RPC client proxy", serviceInterface));
        }

        this.serviceInterface = serviceInterface;
        this.serviceUrl = serviceUrl;

        try
        {
            client = new RpcClient(new URI(serviceUrl), protocol, invoker, eventHandler);
        }
        catch (URISyntaxException e)
        {
            throw new RpcClientInitializationException(e.getMessage(), e);
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object[] _args = args != null ? args : new Object[0];
        for (InvocationExecutor executor : invocationExecutors)
        {
            if (executor.matches(method))
                return executor.invoke(method, _args);
        }
        // will never happen since the last executor is a catch-all
        return null;
    }

    private void logIfUndeclared(Method method, Exception e)
    {
        if (!isRuntimeException(e) && !isDeclaredException(method, e))
        {
            LOGGER.error(format("Undeclared exception of type [%s] is thrown while calling to [%s]", e.getClass(),
                    method), e);
        }
    }

    private boolean isRuntimeException(Exception e)
    {
        return RuntimeException.class.isAssignableFrom(e.getClass());
    }

    private boolean isDeclaredException(Method method, Exception e)
    {
        Class<? extends Exception> thrownExceptionClass = e.getClass();

        Class<?>[] exceptionClasses = method.getExceptionTypes();

        for (Class<?> exceptionClass : exceptionClasses)
        {
            if (exceptionClass.isAssignableFrom(thrownExceptionClass))
                return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcInvoker invoker,
                               RpcClientProtocol protocol)
    {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new RpcClientProxy(serviceInterface, serviceUrl, protocol, invoker, null));
    }


    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcInvoker invoker,
                               RpcClientProtocol protocol, RpcClientEventHandler eventHandler)
    {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new RpcClientProxy(serviceInterface, serviceUrl, protocol, invoker, eventHandler));
    }


    public Class<T> getServiceInterface()
    {
        return serviceInterface;
    }

    public String getServiceUrl()
    {
        return serviceUrl;
    }


    interface InvocationExecutor
    {
        boolean matches(Method method);

        Object invoke(Method method, Object[] args) throws Throwable;
    }


    class ToStringExecutor implements InvocationExecutor
    {

        @Override
        public boolean matches(Method method)
        {
            return method.getName().equals("toString") &&
                    method.getReturnType().equals(String.class) &&
                    method.getParameterTypes().length == 0;
        }

        @Override
        public Object invoke(Method method, Object[] args)
        {
            return format("RPC Proxy for '%s' at URL '%s'", serviceInterface.getName(), serviceUrl);
        }
    }

    class RemoteExecutor implements InvocationExecutor
    {

        @Override
        public boolean matches(Method method)
        {
            return true;
        }

        @Override
        public Object invoke(Method method, Object[] args) throws Throwable
        {
            try
            {
                return client.invoke(method, args);
            }
            catch (RpcInvocationException e)
            {
                // unwrap if applicable
                if (e.hasServerException())
                {
                    Exception serverException = e.getServerException();
                    logIfUndeclared(method, serverException);
                    throw serverException;
                }

                logIfUndeclared(method, e);
                throw e;
            }
            catch (Exception e)
            {
                logIfUndeclared(method, e);
                throw e;
            }
        }
    }

    public RpcClient getClient()
    {
        return client;
    }
}
