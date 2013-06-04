package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * @author alexeyr
 * @since 7/4/11 6:13 PM
 */

public class RpcRequestContext {
    private RpcInvoker invoker;
    private final RpcInvocation invocation;
    private String serviceClass;
    private final String methodName;
    private final String wire;

    RpcRequestContext(RpcInvoker invoker, RpcInvocation invocation, String serviceClass, String methodName, String wire) {
        this.invoker = invoker;
        this.invocation = invocation;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.wire = wire;
    }

    public String getHttpMethod() {
        return invocation.getHttpMethod();
    }


    public URI getUri() {
        return invocation.getServiceUri();
    }


    public MultiMap<String, String> getAllHeaders() {
        return invocation.getAllHeaders();
    }

    public Set<String> getHeaders(String name) {
        return invocation.getAllHeaders().getAll(name);
    }

    public String getHeader(String name) {
        return invocation.getAllHeaders().get(name);
    }

    public void addHeader(String name, String value) {
        invocation.withHeader(name, value);
    }

    public void setHeader(String name, String value) {
        invocation.getAllHeaders().remove(name);
        invocation.withHeader(name, value);
    }

    public boolean hasHeader(String name) {
        return invocation.getAllHeaders().containsKey(name);
    }

    public void setUrlParam(String name, String value) throws URISyntaxException {
        invocation.withQueryParameter(name, value);
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

    public String getServiceClass() {
        return serviceClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcRequestContext that = (RpcRequestContext) o;

        if (invocation != null ? !invocation.equals(that.invocation) : that.invocation != null) return false;
        if (invoker != null ? !invoker.equals(that.invoker) : that.invoker != null) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (serviceClass != null ? !serviceClass.equals(that.serviceClass) : that.serviceClass != null) return false;
        if (wire != null ? !wire.equals(that.wire) : that.wire != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = invoker != null ? invoker.hashCode() : 0;
        result = 31 * result + (invocation != null ? invocation.hashCode() : 0);
        result = 31 * result + (serviceClass != null ? serviceClass.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (wire != null ? wire.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RpcRequestContext{" +
                "invoker=" + invoker +
                ", invocation=" + invocation +
                ", serviceClass='" + serviceClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", wire='" + wire + '\'' +
                '}';
    }
}
