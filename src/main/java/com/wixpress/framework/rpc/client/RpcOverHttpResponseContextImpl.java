package com.wixpress.framework.rpc.client;

import com.wixpress.hoopoe.monads.Either;
import com.wixpress.hoopoe.monads.Pair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alexeyr
 * @since 7/5/11 10:55 AM
 */

public class RpcOverHttpResponseContextImpl implements RpcOverHttpResponseContext
{
    private Either<Throwable, Object> outome;
    private final HttpResponse response;
    private final long requestDurationMillis;

    public RpcOverHttpResponseContextImpl(Throwable throwable, HttpResponse response, long requestDurationMillis)
    {
        this.requestDurationMillis = requestDurationMillis;
        this.outome = Either.Left(throwable);
        this.response = response;
    }

    public RpcOverHttpResponseContextImpl(Object responseObject, HttpResponse response, long requestDurationMillis)
    {
        this.outome = Either.Right(responseObject);
        this.response = response;
        this.requestDurationMillis = requestDurationMillis;
    }

    @Override
    public Either<Throwable, Object> getOutome()
    {
        return outome;
    }

    /**
     * @return
     * @deprecated use RpcOverHttpResponseContext#getOutcome
     */
    @Override
    public Object getResult()
    {
        return outome.getRight();
    }

    @Override
    public void setResult(Object result)
    {
        outome = Either.Right(result);
    }

    @Override
    public List<Pair<String, String>> getAllHeaders()
    {
        return convertHeaders(response.getAllHeaders());
    }

    @Override
    public List<Pair<String, String>> getHeaders(String name)
    {
        return convertHeaders(response.getHeaders(name));
    }

    private List<Pair<String, String>> convertHeaders(Header[] headers)
    {
        List<Pair<String, String>> hdrs = new ArrayList<Pair<String, String>>();
        for (Header h : headers)
        {
            hdrs.add(new Pair<String, String>(h.getName(), h.getValue()));
        }
        return hdrs;
    }

    @Override
    public String getHeader(String name)
    {
       if (response == null)
           return null;

        Header h = response.getFirstHeader(name);
        if (h != null)
            return h.getValue();
        return null;
    }

    public long getRequestDurationMillis()
    {
        return requestDurationMillis;
    }
}
