package com.wixpress.fjarr.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alexeyr
 * @since 6/2/11 2:07 PM
 */

public class RpcInvocation
{

    protected String methodName;
    protected RpcParameters<?> parameters;
    protected Map<String, Object> context = new HashMap<String, Object>();
    protected Exception error = null;
    protected Integer errorCode;
    protected Method resolvedMethod;
    protected Object[] resolvedParameters;
    private Object result;

    public RpcInvocation(Exception error)
    {
        this.error = error;
    }

    public RpcInvocation(String methodName, RpcParameters<?> parameters)
    {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName()
    {
        return methodName;
    }


    public RpcParameters<?> getParameters()
    {
        return parameters;
    }

    public Object getValueFromContext(String key)
    {
        return context.get(key);
    }

    public Object getValueFromContext(String key, Object defaultValue)
    {
        return context.containsKey(key) ? context.get(key) : defaultValue;
    }

    public void putValueToContext(String key, Object value)
    {
        context.put(key, value);
    }

    public RpcInvocation withContextValue(String key, Object value)
    {
        context.put(key, value);
        return this;
    }

    public boolean isError()
    {
        return error != null;
    }

    public Exception getError()
    {
        return error;
    }

    public void setError(Exception error)
    {
        this.error = error;
    }

    public Method getResolvedMethod()
    {
        return resolvedMethod;
    }

    public void setResolvedMethod(Method resolvedMethod)
    {
        this.resolvedMethod = resolvedMethod;
    }

    public Object[] getResolvedParameters()
    {
        return resolvedParameters;
    }

    public void setResolvedParameters(Object[] resolvedParameters)
    {
        this.resolvedParameters = resolvedParameters;
    }

    public void setInvocationResult(Object result)
    {
        this.result = result;
    }

    public Object getResult()
    {
        return result;
    }

    public Integer getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode)
    {
        this.errorCode = errorCode;
    }
}
