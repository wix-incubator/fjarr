package com.wixpress.fjarr.IT.servlet;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.server.RpcServer;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.wixpress.fjarr.IT.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.IT.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletFromWebXmlWithHttpClientTest extends BaseItTest
{

    @BeforeClass
    public static void init() throws Exception
    {
        server = new ITServer(9191, System.getProperty("user.dir") + "/src/test/webapp");

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
