package com.wixpress.fjarr.it.servlet;

import com.wixpress.fjarr.client.RpcClientProtocol;
import com.wixpress.fjarr.client.RpcInvoker;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.example.DataStructServiceImpl;
import com.wixpress.fjarr.it.BaseJsonContractTest;
import com.wixpress.fjarr.it.util.ITServer;
import com.wixpress.fjarr.json.JsonRpc;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

import javax.servlet.Servlet;

import static com.wixpress.fjarr.json.factory.FjarrObjectMapperFactory.anObjectMapperWithFjarrModule;
import static com.wixpress.fjarr.it.factories.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;
import static com.wixpress.fjarr.it.factories.NettyInvokerFactory.aDefaultNettyInvoker;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */
@Ignore("fails in team-city")
public class ServletWithNettyTest extends BaseJsonContractTest {

    @BeforeClass
    public static void init() throws Exception {
        Servlet servlet = new RpcServlet(
                JsonRpc.server(
                        DataStructService.class, new DataStructServiceImpl(), anObjectMapperWithFjarrModule())
        );

        server = new ITServer(SERVER_PORT, new ITServer.ServletPair("/*", servlet));
        server.start();

    }

    @AfterClass
    public static void fini() throws Exception {
        server.stop();
    }

    @Override
    protected RpcClientProtocol anRpcProtocol() {
        return aJsonRpcClientProtocolFrom(anObjectMapperWithFjarrModule());
    }

    @Override
    protected RpcInvoker anRpcInvoker() {
        return aDefaultNettyInvoker();
    }

}
