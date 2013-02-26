package org.wixpress.fjarr.IT.springmvc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wixpress.fjarr.IT.BaseItTest;
import org.wixpress.fjarr.IT.util.ITSpringServer;
import org.wixpress.fjarr.client.NettyClientConfig;
import org.wixpress.fjarr.client.NettyInvoker;
import org.wixpress.fjarr.client.RpcClient;
import org.wixpress.fjarr.client.RpcClientProxy;
import org.wixpress.fjarr.client.exceptions.RpcInvocationException;
import org.wixpress.fjarr.example.DataStructService;
import org.wixpress.fjarr.example.InputDTO;
import org.wixpress.fjarr.json.FjarrJacksonModule;
import org.wixpress.fjarr.json.JsonRpcClientProtocol;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author alex
 * @since 1/6/13 5:29 PM
 */

public class SpringMvcWithNettyClientTest extends BaseItTest
{



    @BeforeClass
    public static void init() throws Exception
    {

        mapper.registerModule(new FjarrJacksonModule());

        server = new ITSpringServer(9191, ITServerConfig.class);

        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        final JsonRpcClientProtocol protocol = new JsonRpcClientProtocol(mapper);
        final NettyInvoker invoker = new NettyInvoker(
                        NettyClientConfig.defaults());
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
