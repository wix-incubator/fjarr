package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * @author alexeyr
 * @since 7/4/11 6:13 PM
 */

public class RpcRequestContext
{
    private RpcInvoker invoker;
    private final RpcInvocation invocation;
    private String serviceClass;
    private final String methodName;
    private final String wire;

    RpcRequestContext(RpcInvoker invoker, RpcInvocation invocation, String serviceClass, String methodName, String wire)
    {
        this.invoker = invoker;
        this.invocation = invocation;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.wire = wire;
    }

    public String getHttpMethod()
    {
        return invocation.getHttpMethod();
    }


    public URI getUri()
    {
        return invocation.getServiceUri();
    }


    public MultiMap<String,String> getAllHeaders()
    {
        return invocation.getAllHeaders();
    }

    public Set<String> getHeaders(String name)
    {
        return invocation.getAllHeaders().getAll(name);
    }

    public String getHeader(String name)
    {
        return invocation.getAllHeaders().get(name);
    }

    public void addHeader(String name, String value)
    {
        invocation.withHeader(name, value);
    }

    public void setHeader(String name, String value)
    {
        invocation.getAllHeaders().remove(name);
        invocation.withHeader(name,value);
    }

    public boolean hasHeader(String name)
    {
        return invocation.getAllHeaders().containsKey(name);
    }

    public void setUrlParam(String name, String value) throws URISyntaxException
    {
        invocation.withQueryParameter(name,value);
    }

    public RpcInvocation getRpcInvocation() {
        return invocation;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getWire() {
        return wire;
    }

    public String getServiceClass()
    {
        return serviceClass;
    }
}
