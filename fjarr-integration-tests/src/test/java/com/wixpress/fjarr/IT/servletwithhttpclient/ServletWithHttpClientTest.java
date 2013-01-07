package com.wixpress.fjarr.IT.servletwithhttpclient;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITServer;
import com.wixpress.fjarr.client.ApacheHttpClient4Factory;
import com.wixpress.fjarr.client.HttpClientConfig;
import com.wixpress.fjarr.client.HttpComponentsInvoker;
import com.wixpress.fjarr.client.RpcClientProxy;
import com.wixpress.fjarr.example.DataStructService;
import com.wixpress.fjarr.example.DataStructServiceImpl;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import com.wixpress.fjarr.json.JsonRpcClientProtocol;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.server.RpcServer;
import com.wixpress.fjarr.server.RpcServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.servlet.Servlet;

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
                        DataStructService.class));

        server = new ITServer(9191, new ITServer.ServletPair("/*", servlet));

        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        service = RpcClientProxy.create(DataStructService.class,
                serviceRoot,
                new HttpComponentsInvoker(
                        new ApacheHttpClient4Factory(
                                HttpClientConfig.defaults())),
                new JsonRpcClientProtocol(mapper));

        server.start();

    }

    @AfterClass
    public static void fini() throws Exception
    {
        server.stop();
    }





}
