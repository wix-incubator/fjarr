package org.wixpress.fjarr.client;

/**
 * @author alexeyr
 * @since 7/4/11 5:37 PM
 */

public interface RpcClientEventHandler
{
    void preInvoke(RpcRequestContext context);

    void postInvoke(RpcRequestContext requestContext, RpcResponseContext responseContext);

}
