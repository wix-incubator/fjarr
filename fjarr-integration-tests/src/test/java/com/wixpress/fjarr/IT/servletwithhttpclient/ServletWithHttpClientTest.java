package com.wixpress.fjarr.IT.servletwithhttpclient;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.example.DataStructServiceImpl;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import com.wixpress.fjarr.json.JsonRpcClientProtocol;
import com.wixpress.fjarr.json.JsonRpcExtensionMethodExecutor;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.json.extensionmethods.ServiceNameExtensionMethod;
import com.wixpress.fjarr.server.RpcServer;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.Servlet;
import java.net.URI;

/**
 * @author alex
 * @since 1/6/13 1:57 PM
 */

public class ServletWithHttpClientTest extends BaseItTest
{

    @BeforeClass
    public static void init() throws Exception
    {

        mapper.registerModule(new FjarrJacksonModule());
        Servlet servlet = new RpcServlet(
                new RpcServer(
                        new JsonRpcProtocol(
                                mapper),
                        new DataStructServiceImpl(),
                        DataStructService.class,
                        new JsonRpcExtensionMethodExecutor(
                                new ServiceNameExtensionMethod(DataStructService.class)
                        )));

        server = new ITServer(9191, new ITServer.ServletPair("/*", servlet));

        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        final JsonRpcClientProtocol protocol = new JsonRpcClientProtocol(mapper);
        final HttpComponentsInvoker invoker = new HttpComponentsInvoker(
                new ApacheHttpClient4Factory(
                        HttpClientConfig.defaults()));
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
