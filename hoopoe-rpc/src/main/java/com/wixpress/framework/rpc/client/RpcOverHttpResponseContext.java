package com.wixpress.framework.rpc.client;

import com.wixpress.fjarr.monads.Either;
import com.wixpress.fjarr.monads.Pair;

import java.util.List;

/**
 * @author alexeyr
 * @since 7/4/11 5:42 PM
 */

public interface RpcOverHttpResponseContext
{
    /**
     * @deprecated use RpcOverHttpResponseContext#getOutcome instead
     * @return
     */
    @Deprecated
    public Object getResult();

    public void setResult(Object result);

    public List<Pair<String, String>> getAllHeaders();

    public List<Pair<String, String>> getHeaders(String name);

    public String getHeader(String name);

    public Either<Throwable, Object> getOutome();

    public long getRequestDurationMillis();
}
