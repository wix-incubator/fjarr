package org.wixpress.fjarr.client;

import org.wixpress.fjarr.util.DisjointUnion;
import org.wixpress.fjarr.util.MultiMap;

import java.util.Set;

/**
 * @author alexeyr
 * @since 7/5/11 10:55 AM
 */

public class RpcResponseContext
{
    private DisjointUnion outome;
    private final RpcInvocationResponse response;
    private final long requestDurationMillis;

    public RpcResponseContext(Throwable throwable, RpcInvocationResponse response, long requestDurationMillis)
    {
        this.requestDurationMillis = requestDurationMillis;
        this.outome = DisjointUnion.from(throwable);
        this.response = response;
    }

    public RpcResponseContext(Object responseObject, RpcInvocationResponse response, long requestDurationMillis)
    {
        this.outome = DisjointUnion.from(responseObject);
        this.response = response;
        this.requestDurationMillis = requestDurationMillis;
    }

    public DisjointUnion getOutome()
    {
        return outome;
    }


    public void setResult(Object result)
    {
        outome = DisjointUnion.from(result);
    }

    public MultiMap<String, String> getAllHeaders()
    {
        return response.getAllHeaders();
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
