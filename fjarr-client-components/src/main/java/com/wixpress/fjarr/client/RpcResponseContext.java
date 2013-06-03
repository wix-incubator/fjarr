package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.DisjointUnion;
import com.wixpress.fjarr.util.MultiMap;

import java.util.Set;

/**
 * @author alexeyr
 * @since 7/5/11 10:55 AM
 */

public class RpcResponseContext
{
    private final DisjointUnion outcome;
    private final boolean error;
    private final RpcInvocationResponse response;
    private final long requestDurationMillis;

    public RpcResponseContext(Throwable throwable, RpcInvocationResponse response, long requestDurationMillis)
    {
        this(throwable,null,response,requestDurationMillis,true);
    }

    public RpcResponseContext(Object responseObject, RpcInvocationResponse response, long requestDurationMillis)
    {
        this(null,responseObject,response,requestDurationMillis,false);
    }

    private RpcResponseContext(Throwable throwable, Object responseObject, RpcInvocationResponse response,
                               long requestDurationMillis,boolean error){
        this.outcome = DisjointUnion.from( error ? throwable : responseObject);
        this.response = (response != null ? response : new RpcInvocationResponse(-1,"","",new MultiMap<String, String>()));
        this.requestDurationMillis = requestDurationMillis;
        this.error = error;

    }
    /**
     * DisjointUnion that can contain an Object or a Throwable
     * @return
     */
    public DisjointUnion getOutcome()
    {
        return outcome;
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
