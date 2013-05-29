package com.wixpress.fjarr.IT.servlet;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.example.DataStructServiceImpl;
import com.wixpress.fjarr.json.JsonRpc;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.Servlet;

import static com.wixpress.fjarr.IT.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.IT.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletWithHttpClientTest extends BaseItTest
{

    @BeforeClass
    public static void init() throws Exception
    {
        Servlet servlet = new RpcServlet(
                JsonRpc.server(
                        DataStructService.class, new DataStructServiceImpl(), buildObjectMapperWithFjarrModule())
        );

        server = new ITServer(9191, new ITServer.ServletPair("/*", servlet));

        server.start();

    }

    @AfterClass
    public static void fini() throws Exception
    {
        server.stop();
    }

    @Override
    protected RpcClientProtocol getProtocol() {
        return aJsonRpcClientProtocolFrom(buildObjectMapperWithFjarrModule());
    }

    @Override
    protected RpcInvoker getInvoker() {
        return aDefaultHttpComponentsInvoker();
    }

}
