package com.wixpress.framework.rpc.client;

/**
 * @author alex
 * @since 11/1/11 3:55 PM
 */
@Deprecated
public interface RetryStrategy
{
    boolean processConnectionError(Exception e, int attempt);
    boolean processProtocolError(Exception e, int attempt);
}
