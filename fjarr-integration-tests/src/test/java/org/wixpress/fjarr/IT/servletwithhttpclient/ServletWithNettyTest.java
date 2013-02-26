package org.wixpress.fjarr.IT.servletwithhttpclient;

import org.wixpress.fjarr.IT.BaseItTest;
import org.wixpress.fjarr.IT.util.ITServer;
import org.wixpress.fjarr.client.NettyClientConfig;
import org.wixpress.fjarr.client.NettyInvoker;
import org.wixpress.fjarr.client.RpcClient;
import org.wixpress.fjarr.client.RpcClientProxy;
import org.wixpress.fjarr.example.DataStructService;
import org.wixpress.fjarr.example.DataStructServiceImpl;
import org.wixpress.fjarr.json.FjarrJacksonModule;
import org.wixpress.fjarr.json.JsonRpc;
import org.wixpress.fjarr.json.JsonRpcClientProtocol;
import org.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.Servlet;
import java.net.URI;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */


public class ServletWithNettyTest extends BaseItTest
{

    @BeforeClass
    public static void init() throws Exception
    {

        mapper.registerModule(new FjarrJacksonModule());
        Servlet servlet = new RpcServlet(
                JsonRpc.server(
                        DataStructService.class, new DataStructServiceImpl(), mapper)
        );

        server = new ITServer(9191, new ITServer.ServletPair("/*", servlet));

        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        final JsonRpcClientProtocol protocol = new JsonRpcClientProtocol(mapper);
        final NettyInvoker invoker = new NettyInvoker(
                NettyClientConfig.defaults());
        service = RpcClientProxy.create(DataStructService.class,
                serviceRoot,
                invoker,
                protocol);

        client = new RpcClient(new URI(serviceRoot), protocol, invoker, null);

        server.start();

    }

    @AfterClass
    public static void fini() throws Exception
    {
        server.stop();
    }


}
