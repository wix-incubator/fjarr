package com.wixpress.fjarr.it;

import com.wixpress.fjarr.client.RpcClient;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author: ittaiz
 * @since: 5/30/13
 */
public abstract class BaseJsonContractTest extends BaseContractTest {
    protected RpcClient client;

    @Before
    public void setupClientSide() throws URISyntaxException {
        super.setupClientSide();
        client = new RpcClient(new URI(serviceRoot), protocol, invoker);
    }

    @Test
    public void testDescribe() throws Throwable {
        String response = client.invoke("aaa", "rpc.getServiceName", String.class);
        assertThat(response, is("com.wixpress.fjarr.example.DataStructService"));
    }
}
