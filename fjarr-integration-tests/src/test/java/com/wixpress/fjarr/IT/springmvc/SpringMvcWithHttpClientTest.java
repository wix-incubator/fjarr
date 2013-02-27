package com.wixpress.fjarr.IT.springmvc;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITSpringServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.client.exceptions.RpcInvocationException;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.example.InputDTO;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import com.wixpress.fjarr.json.JsonRpcClientProtocol;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author alex
 * @since 1/6/13 5:29 PM
 */

public class SpringMvcWithHttpClientTest extends BaseItTest
{



    @BeforeClass
    public static void init() throws Exception
    {

        mapper.registerModule(new FjarrJacksonModule());

        server = new ITSpringServer(9191, ITServerConfig.class);

        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        final JsonRpcClientProtocol protocol = new JsonRpcClientProtocol(mapper);
        final HttpComponentsInvoker invoker = new HttpComponentsInvoker(
                new ApacheHttpClient4Factory(
                        HttpClientConfig.defaults()));
        service = RpcClientProxy.create(DataStructService.class,
                serviceRoot,
                invoker,
                protocol);

        server.start();

        client = new RpcClient(new URI(serviceRoot), protocol, invoker, null);

    }

    @AfterClass
    public static void fini() throws Exception
    {
        server.stop();
    }


    @Test
    public void testValidationSuccess()
    {
        service.withInputThatNeedsValidation(new InputDTO(""));
    }

    @Test
    public void testValidationFailure()
    {
        try
        {
            service.withInputThatNeedsValidation(new InputDTO());
        }
        catch (RpcInvocationException e)
        {
            assertThat(e.getMessage(), is("JSON-RPC Error -32603: \"Validation failed\""));
        }
    }
}
