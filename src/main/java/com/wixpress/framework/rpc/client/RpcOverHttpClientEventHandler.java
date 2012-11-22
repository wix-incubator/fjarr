package com.wixpress.framework.rpc.client;

/**
 * @author alexeyr
 * @since 7/4/11 5:37 PM
 */

public interface RpcOverHttpClientEventHandler
{
    void preInvoke(RpcOverHttpRequestContext context);

    void postInvoke(RpcOverHttpRequestContext requestContext, RpcOverHttpResponseContext responseContext);

}
