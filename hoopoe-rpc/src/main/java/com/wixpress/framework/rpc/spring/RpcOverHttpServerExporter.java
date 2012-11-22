package com.wixpress.framework.rpc.spring;


import com.wixpress.framework.rpc.server.RpcOverHttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author alexeyr
 * @since 7/4/11 4:35 PM
 */


public class RpcOverHttpServerExporter extends RemoteExporter implements HttpRequestHandler
{
    @Autowired
    private RpcOverHttpServer server;

    public RpcOverHttpServerExporter(RpcOverHttpServer server)
    {
        this.server = server;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        server.handleRequest(request, response);
    }

}
