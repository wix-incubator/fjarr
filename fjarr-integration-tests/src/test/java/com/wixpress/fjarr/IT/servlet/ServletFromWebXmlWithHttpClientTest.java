package com.wixpress.fjarr.IT.servlet;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import com.wixpress.fjarr.json.JsonRpcClientProtocol;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.server.RpcServer;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletFromWebXmlWithHttpClientTest extends BaseItTest
{

    @BeforeClass
    public static void init() throws Exception
    {
        mapper.registerModule(new FjarrJacksonModule());
        server = new ITServer(9191, System.getProperty("user.dir") + "/src/test/webapp");

        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        final JsonRpcClientProtocol protocol = new JsonRpcClientProtocol(mapper);
        final HttpComponentsInvoker invoker = new HttpComponentsInvoker(
                new ApacheHttpClient4Factory(
                        HttpClientConfig.defaults()));
        service = RpcClientProxy.create(DataStructService.class,
                serviceRoot,
                invoker,
                protocol);

        client = new RpcClient(new URI(serviceRoot), protocol, invoker);

        server.start();

    }

    @AfterClass
    public static void fini() throws Exception
    {
        server.stop();
    }

    @Override
    @Test
    public void testDescribe() throws Throwable
    {
        // This dest doesn't apply
    }

    @Test
    public void checkServletContextValues()
    {
        final JsonRpcProtocol protocol = (JsonRpcProtocol) server.getContextAttribute(RpcServlet.FJARR_PROTOCOL_ATTRIBUTE);
        final RpcServer rpcServer = (RpcServer)server.getContextAttribute(RpcServlet.FJARR_SERVER_ATTRIBUTE);

    }
}
