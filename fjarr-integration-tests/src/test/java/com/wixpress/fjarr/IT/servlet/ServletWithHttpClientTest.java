package com.wixpress.fjarr.it.servlet;

import com.wixpress.fjarr.it.BaseContractTest;
import com.wixpress.fjarr.it.BaseJsonContractTest;
import com.wixpress.fjarr.it.util.ITServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.example.DataStructServiceImpl;
import com.wixpress.fjarr.json.JsonRpc;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.Servlet;

import static com.wixpress.fjarr.it.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.it.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletWithHttpClientTest extends BaseJsonContractTest
{

    @BeforeClass
    public static void init() throws Exception
    {
        Servlet servlet = new RpcServlet(
                JsonRpc.server(
                        DataStructService.class, new DataStructServiceImpl(), buildObjectMapperWithFjarrModule())
        );

        server = new ITServer(SERVER_PORT, new ITServer.ServletPair("/*", servlet));

        server.start();

    }

    @AfterClass
    public static void fini() throws Exception
    {
        server.stop();
    }

    @Override
    protected RpcClientProtocol buildProtocol() {
        return aJsonRpcClientProtocolFrom(buildObjectMapperWithFjarrModule());
    }

    @Override
    protected RpcInvoker buildInvoker() {
        return aDefaultHttpComponentsInvoker();
    }

}
