package com.wixpress.framework.rpc.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author AlexeyR
 * @since 6/15/11 8:44 PM
 */

public class RpcRequest
{
    private List<RpcInvocation> invocations;
    private Multimap<String, String> queryParams;
    private String rawRequestBody;

    private RpcRequestStatistics statistics;

    public RpcRequest(String rawRequestBody, List<RpcInvocation> invocations, Multimap<String, String> queryParams)
    {
        this.invocations = invocations;
        this.queryParams = queryParams;
        this.rawRequestBody = rawRequestBody;
    }

    public RpcRequest(List<RpcInvocation> invocations, Multimap<String, String> queryParams)
    {
        this.invocations = invocations;
        this.queryParams = queryParams;
        this.rawRequestBody = "";
    }

    public RpcRequest(String rawRequestBody)
    {
        this(rawRequestBody, new ArrayList<RpcInvocation>(), HashMultimap.<String, String>create());
    }


    public RpcRequest()
       {
           this( new ArrayList<RpcInvocation>(), HashMultimap.<String, String>create());
       }
    public Multimap<String, String> getQueryParams()
    {
        return queryParams;
    }

    public void setQueryParams(Multimap<String, String> queryParams)
    {
        this.queryParams = queryParams;
    }

    public List<RpcInvocation> getInvocations()
    {
        return invocations;
    }

    public void setInvocations(List<RpcInvocation> invocations)
    {
        this.invocations = invocations;
    }

    public String getRawRequestBody()
    {
        return rawRequestBody;
    }

    public void setRawRequestBody(String rawRequestBody)
    {
        this.rawRequestBody = rawRequestBody;
    }

    public RpcRequestStatistics getStatistics()
    {
        return statistics;
    }

    public void setStatistics(RpcRequestStatistics statistics)
    {
        this.statistics = statistics;
    }

    public static RpcRequest of(String rawRequestBody, RpcInvocation... calls)
    {
        return new RpcRequest(rawRequestBody, newArrayList(calls), HashMultimap.<String, String>create());
    }

    public static RpcRequest of(RpcInvocation... calls)
    {
        return new RpcRequest(newArrayList(calls), HashMultimap.<String, String>create());
    }
}
