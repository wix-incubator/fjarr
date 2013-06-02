package com.wixpress.fjarr.it;

import com.wixpress.fjarr.client.RpcClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

    @Test
    @Ignore("Fails inconsistently with java.net.SocketException: Unexpected end of file aNettyInvokerFrom server")
    public void testInvalidJsonReturnsHttpStatus400() throws Exception {
        RestTemplate template = new RestTemplate();
        String content = "{ \"some\": \"invalid request\" ]";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json-rpc");
        headers.add("Content-Length", Integer.toString(content.getBytes().length));
        HttpEntity<String> request = new HttpEntity<String>(content, headers);
        try {
            template.exchange(serviceRoot, HttpMethod.POST, request, String.class);
            fail("Exception expected here");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        }
    }


}
