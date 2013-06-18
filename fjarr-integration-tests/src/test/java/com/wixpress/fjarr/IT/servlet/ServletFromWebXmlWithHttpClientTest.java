package com.wixpress.fjarr.it.servlet;

import com.wixpress.fjarr.client.RpcClientProtocol;
import com.wixpress.fjarr.client.RpcInvoker;
import com.wixpress.fjarr.it.BaseContractTest;
import com.wixpress.fjarr.it.util.ITServer;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.server.RpcServer;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.wixpress.fjarr.json.factory.FjarrObjectMapperFactory.anObjectMapperWithFjarrModule;
import static com.wixpress.fjarr.it.factories.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.it.factories.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletFromWebXmlWithHttpClientTest extends BaseContractTest {

    @BeforeClass
    public static void init() throws Exception {
        server = new ITServer(SERVER_PORT, System.getProperty("user.dir") + "/src/test/webapp");

        server.start();

    }

    @AfterClass
    public static void fini() throws Exception {
        server.stop();
    }

    @Test
    public void checkServletContextValues() {
        final JsonRpcProtocol protocol = (JsonRpcProtocol) server.getContextAttribute(RpcServlet.FJARR_PROTOCOL_ATTRIBUTE);
        final RpcServer rpcServer = (RpcServer) server.getContextAttribute(RpcServlet.FJARR_SERVER_ATTRIBUTE);

    }

    @Override
    protected RpcClientProtocol anRpcProtocol() {
        return aJsonRpcClientProtocolFrom(anObjectMapperWithFjarrModule());
    }

    @Override
    protected RpcInvoker anRpcInvoker() {
        return aDefaultHttpComponentsInvoker();
    }
}
