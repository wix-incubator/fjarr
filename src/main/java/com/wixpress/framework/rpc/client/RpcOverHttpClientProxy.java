package com.wixpress.framework.rpc.client;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.wixpress.framework.rpc.client.exceptions.RpcClientInitializationException;
import com.wixpress.framework.rpc.client.exceptions.RpcInvocationException;
import com.wixpress.hoopoe.monads.Pair;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.wixpress.framework.rpc.client.HttpClientConfig.defaults;
import static java.lang.String.format;

/**
 * @author shaiyallin
 * @since 1/3/12
 */
public class RpcOverHttpClientProxy<T> implements MethodInterceptor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcOverHttpClientProxy.class);

    private final Class<T> serviceInterface;
    private final String serviceUrl;

    private final RpcOverHttpClient client;

    @Deprecated
    private RpcOverHttpClientProxy(Class<T> serviceInterface, String serviceUrl,
                                   RpcProtocolClient protocol, HttpClientConfig httpClientConfig,
                                   RpcOverHttpClientEventHandler eventHandler, RetryStrategy retryStrategy)
    {
       this(serviceInterface, serviceUrl, protocol, httpClientConfig, eventHandler);
    }

    private RpcOverHttpClientProxy(Class<T> serviceInterface, String serviceUrl,
                                   RpcProtocolClient protocol, HttpClientConfig httpClientConfig,
                                   RpcOverHttpClientEventHandler eventHandler)
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
            client = new RpcOverHttpClient(new URI(serviceUrl), protocol, httpClientConfig, eventHandler);
        }
        catch (URISyntaxException e)
        {
            throw new RpcClientInitializationException(e.getMessage(), e);
        }
    }

    private final List<Pair<Predicate<MethodInvocation>, LocallyCallable>> localInvocations = Lists.newArrayList(
            Pair.<Predicate<MethodInvocation>, LocallyCallable>of(new ToStringPredicate(), new ToStringCallable()));

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        //invocation.getMethod().getGenericReturnType()
        if (isLocal(invocation))
            return executeLocally(invocation);

        try
        {
            return client.invoke(invocation.getMethod(), invocation.getArguments());
        }
        catch (RpcInvocationException e)
        {
            // unwrap if applicable
            if (e.hasServerException())
            {
                Exception serverException = e.getServerException();
                logIfUndeclared(invocation, serverException);
                throw serverException;
            }

            logIfUndeclared(invocation, e);
            throw e;
        }
        catch (Exception e)
        {
            logIfUndeclared(invocation, e);
            throw e;
        }
    }

    private void logIfUndeclared(MethodInvocation methodInvocation, Exception e)
    {
        if (!isRuntimeException(e) && !isDeclaredException(methodInvocation, e))
        {
            LOGGER.error(format("Undeclared exception of type [%s] is thrown while calling to [%s]", e.getClass(),
                    methodInvocation), e);
        }
    }

    private boolean isRuntimeException(Exception e)
    {
        return RuntimeException.class.isAssignableFrom(e.getClass());
    }

    private boolean isDeclaredException(MethodInvocation methodInvocation, Exception e)
    {
        Class<? extends Exception> thrownExceptionClass = e.getClass();

        Class<?>[] exceptionClasses = methodInvocation.getMethod().getExceptionTypes();

        for (Class<?> exceptionClass : exceptionClasses)
        {
            if (exceptionClass.isAssignableFrom(thrownExceptionClass))
                return true;
        }

        return false;
    }

    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcProtocolClient protocol)
    {
        return create(serviceInterface, serviceUrl, protocol, defaults(), null, null);
    }

    @Deprecated
    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcProtocolClient protocol,
                               HttpClientConfig httpClientConfig, RpcOverHttpClientEventHandler eventHandler,
                               RetryStrategy retryStrategy)
    {
        return create(serviceInterface, serviceUrl, protocol, httpClientConfig, eventHandler);
    }

    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcProtocolClient protocol,
                               HttpClientConfig httpClientConfig, RpcOverHttpClientEventHandler eventHandler)
    {
        RpcOverHttpClientProxy<T> clientProxy = new RpcOverHttpClientProxy<T>(
                serviceInterface, serviceUrl, protocol, httpClientConfig, eventHandler);

        return ProxyFactory.getProxy(serviceInterface, clientProxy);
    }

    @Deprecated
    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcProtocolClient protocol,
                               int connectionTimeoutInSeconds)
    {
        return create(serviceInterface, serviceUrl, protocol,
                defaults().withConnectionTimeoutMillis(secondsToMillis(connectionTimeoutInSeconds)),
                null);
    }

    private static int secondsToMillis(int connectionTimeoutInSeconds)
    {
        return connectionTimeoutInSeconds * 1000;
    }

    @Deprecated
    public static <T> T create(Class<T> serviceInterface, String serviceUrl,
                               RpcProtocolClient protocol, int connectionTimeoutInSeconds,
                               RpcOverHttpClientEventHandler eventHandler)
    {

        return create(serviceInterface, serviceUrl, protocol,
                defaults().withConnectionTimeoutMillis(secondsToMillis(connectionTimeoutInSeconds)),
                eventHandler);
    }

    @Deprecated
    public static <T> T create(Class<T> serviceInterface, String serviceUrl, RpcProtocolClient protocol,
                               int connectionTimeoutInSeconds, RpcOverHttpClientEventHandler eventHandler,
                               RetryStrategy retryStrategy)
    {

        RpcOverHttpClientProxy<T> clientProxy = new RpcOverHttpClientProxy<T>(
                serviceInterface, serviceUrl, protocol,
                defaults().withConnectionTimeoutMillis(secondsToMillis(connectionTimeoutInSeconds)),
                eventHandler);

        return ProxyFactory.getProxy(serviceInterface, clientProxy);
    }

    private Object executeLocally(MethodInvocation invocation) throws Exception
    {
        LocallyCallable locallyCallable = null;
        for (Pair<Predicate<MethodInvocation>, LocallyCallable> pair : localInvocations)
        {
            if (pair.left.apply(invocation))
            {
                locallyCallable = pair.right;
                break;
            }
        }
        return locallyCallable.call(invocation);
    }

    private boolean isLocal(MethodInvocation invocation)
    {
        for (Pair<Predicate<MethodInvocation>, LocallyCallable> pair : localInvocations)
            if (pair.left.apply(invocation))
                return true;

        return false;
    }

    public Class<T> getServiceInterface()
    {
        return serviceInterface;
    }

    public String getServiceUrl()
    {
        return serviceUrl;
    }

    interface LocallyCallable
    {
        Object call(MethodInvocation invocation) throws Exception;
    }

    class ToStringCallable implements LocallyCallable
    {
        @Override
        public String call(MethodInvocation invocation) throws Exception
        {
            return format("RPC Proxy for '%s' at URL '%s'", serviceInterface.getName(), serviceUrl);
        }
    }

    class ToStringPredicate implements Predicate<MethodInvocation>
    {
        @Override
        public boolean apply(MethodInvocation input)
        {
            Method method = input.getMethod();
            return method.getName().equals("toString") &&
                    method.getReturnType().equals(String.class) &&
                    method.getParameterTypes().length == 0;
        }
    }

}
