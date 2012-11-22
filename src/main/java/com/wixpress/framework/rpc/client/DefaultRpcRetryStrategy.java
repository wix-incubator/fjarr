package com.wixpress.framework.rpc.client;


import com.wixpress.framework.rpc.client.exceptions.RpcTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * @author alex
 * @since 12/4/11 3:18 PM
 */
@Deprecated
public class DefaultRpcRetryStrategy implements RetryStrategy
{

    Logger log = LoggerFactory.getLogger(DefaultRpcRetryStrategy.class);

    protected int connectionRetryCount;
    protected Set<Integer> retryOnStatusCodes;
    protected int protocolRetryCount;

    public DefaultRpcRetryStrategy(int connectionRetryCount, Set<Integer> retryOnStatusCodes, int protocolRetryCount)
    {

        this.connectionRetryCount = connectionRetryCount;
        this.retryOnStatusCodes = retryOnStatusCodes;
        this.protocolRetryCount = protocolRetryCount;
    }

    @Override
    public boolean processConnectionError(Exception exception, int attempt)
    {
        if (exception == null)
        {
            throw new IllegalArgumentException("Exception parameter may not be null");
        }
        if (attempt > this.connectionRetryCount)
        {
            // Do not retry if over max retry count
            log.warn("Number of retries exceeded connectionRetryCount, failing", exception);
            return false;
        }
//
//        if (exception instanceof UnknownHostException)
//        {
//            // Unknown host
//            return false;
//        }
//        if (exception instanceof ConnectException)
//        {
//            // Connection refused
//            return false;
//        }
//        if (exception instanceof SSLException)
//        {
//            // SSL handshake exception
//            return false;
//        }

        if (exception instanceof IOException)
        {
            log.warn("Retrying connection because of exception",exception);
            return true;
        }
        log.warn("Failing connection because of exception",exception);
        return false;
    }

    @Override
    public boolean processProtocolError(Exception e, int attempt)
    {
        if (e instanceof RpcTransportException)
        {
            if (retryOnStatusCodes.contains(((RpcTransportException) e).getStatusCode()) &&
                    protocolRetryCount > attempt)
            {
                log.warn("retrying PRC invocation because of exception",e);
                return true;
            }
        }
        log.warn("Failing RPC invocation", e);
        return false;
    }
}
