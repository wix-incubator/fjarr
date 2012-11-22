package com.wixpress.framework.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.framework.rpc.client.RetryStrategy;
import com.wixpress.framework.rpc.client.RpcOverHttpClientProxy;
import com.wixpress.framework.rpc.client.exceptions.RpcTransportException;
import com.wixpress.framework.rpc.json.JsonRpcProtocolClient;
import org.apache.commons.lang.mutable.MutableInt;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author alex
 * @since 2/16/12 4:20 PM
 */

@Ignore ("This feature is deprecated")
public class RetryStrategyTest
{

    @Test
    public void retryStrategyTest()
    {

        final MutableInt errors = new MutableInt(0);

        TestService p = RpcOverHttpClientProxy.create(TestService.class, "http://localhost:6464/TestService",
                new JsonRpcProtocolClient(new ObjectMapper()), 10, null, new RetryStrategy()
        {
            @Override
            public boolean processConnectionError(Exception e, int attempt)
            {
                errors.increment();
                return false;
            }

            @Override
            public boolean processProtocolError(Exception e, int attempt)
            {
                errors.increment();
                return false;
            }
        });
        try
        {
            p.invoke();
        }
        catch (RpcTransportException e)
        {

        }

        assertThat(errors.intValue(), is(1));
    }


    public interface TestService
    {
        void invoke();
    }
}
