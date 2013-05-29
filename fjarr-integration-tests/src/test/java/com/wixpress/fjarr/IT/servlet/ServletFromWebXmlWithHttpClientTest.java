package com.wixpress.fjarr.it.servlet;

import com.wixpress.fjarr.it.BaseContractTest;
import com.wixpress.fjarr.it.util.ITServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.server.RpcServer;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.wixpress.fjarr.it.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.it.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletFromWebXmlWithHttpClientTest extends BaseContractTest
{

    @BeforeClass
    public static void init() throws Exception
    {
        server = new ITServer(SERVER_PORT, System.getProperty("user.dir") + "/src/test/webapp");

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

    @Override
    protected RpcClientProtocol getProtocol() {
        return aJsonRpcClientProtocolFrom(buildObjectMapperWithFjarrModule());
    }

    @Override
    protected RpcInvoker getInvoker() {
        return aDefaultHttpComponentsInvoker();
    }
}
