package com.wixpress.fjarr.IT.util;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

/**
 * @author alex
 * @since 1/6/13 12:11 PM
 */

public class ITServer
{
    public static final Logger logger = LoggerFactory.getLogger(ITServer.class);
    public static final int DEFAULT_PORT = 9901;
    public static final String ROOT = "/";
    protected int port;
    protected ServletPair[] servlets;
    protected Server server;


    public ITServer()
    {
        this(DEFAULT_PORT);
    }


    public ITServer(int port, ServletPair... servlets)
    {

        this.port = port;
        this.servlets = servlets;
    }


    public void start() throws Exception
    {
        server = new Server();
        logger.info("Starting jetty on port " + port);

        Connector con = new SelectChannelConnector();
        con.setPort(port);
        server.addConnector(con);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(ROOT);
        server.setHandler(context);

        for (ServletPair sp : servlets)
        {
            context.addServlet(new ServletHolder(sp.servlet), sp.path);
        }

        server.start();
//        server.join();
        logger.info("Jetty server started.");
    }

    public void stop() throws Exception
    {
        server.stop();
        server.destroy();
        server = null;
    }

    public static class ServletPair
    {
        public String path;
        public Servlet servlet;

        public ServletPair(String path, Servlet servlet)
        {
            this.path = path;
            this.servlet = servlet;
        }
    }

}
