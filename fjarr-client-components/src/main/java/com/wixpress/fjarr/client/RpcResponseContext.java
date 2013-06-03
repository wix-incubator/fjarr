package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;

import java.util.Set;

/**
 * @author alexeyr
 * @since 7/5/11 10:55 AM
 */

public class RpcResponseContext
{
    private final Throwable thrown;
    private final boolean error;
    private final RpcInvocationResponse response;
    private final long requestDurationMillis;

    public RpcResponseContext(Throwable throwable, RpcInvocationResponse response, long requestDurationMillis)
    {
        this(throwable,response,requestDurationMillis,true);
    }

    public RpcResponseContext(RpcInvocationResponse response, long requestDurationMillis)
    {
        this(null,response,requestDurationMillis,false);
    }

    private RpcResponseContext(Throwable throwable, RpcInvocationResponse response,
                               long requestDurationMillis,boolean error){
        this.thrown = throwable;
        this.response = (response != null ? response : new RpcInvocationResponse(-1,"","",new MultiMap<String, String>()));
        this.requestDurationMillis = requestDurationMillis;
        this.error = error;

    }

    public Throwable getThrown()
    {
        return thrown;
    }

    public boolean isError()
    {
        return error;
    }

    public Set<String> getHeaders(String name)
    {
        return response.getAllHeaders().getAll(name);
    }

    public String getHeader(String name)
    {
        return response.getAllHeaders().get(name);
    }

    public long getRequestDurationMillis()
    {
        return requestDurationMillis;
    }
}
