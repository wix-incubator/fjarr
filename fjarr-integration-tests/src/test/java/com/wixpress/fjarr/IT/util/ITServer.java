package com.wixpress.fjarr.it.util;

import com.wixpress.fjarr.util.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

/**
 * @author alex
 * @since 1/6/13 12:11 PM
 */

public class ITServer {
    public static final Logger logger = LoggerFactory.getLogger(ITServer.class);
    public static final int DEFAULT_PORT = 9901;
    public static final String ROOT = "/";
    protected int port;
    protected ServletPair[] servlets;
    protected Server server;
    protected String webAppPath;
    private ServletContextHandler context;

    public ITServer(int port, ServletPair... servlets) {
        this.port = port;
        this.servlets = servlets;
    }

    public ITServer(int port, String webAppPath) {
        this.port = port;
        this.webAppPath = webAppPath;
    }


    public void start() throws Exception {
        server = new Server();
        logger.info("Starting jetty on port " + port);

        Connector con = new SelectChannelConnector();
        con.setPort(port);
        server.addConnector(con);

        context = null;
        if (servlets != null)
            context = contextFromServletPairs();
        else if (StringUtils.isNotBlank(webAppPath))
            context = contextFromWebXml();

        if (context == null) {
            logger.info("Jetty server wasn't configured properly.");
            throw new RuntimeException("Bad configuration");
        }

        server.setHandler(context);
        server.start();
//        server.join();
        logger.info("Jetty server started.");

    }

    private ServletContextHandler contextFromServletPairs() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(ROOT);

        for (ServletPair sp : servlets) {
            context.addServlet(new ServletHolder(sp.servlet), sp.path);
        }
        return context;
    }


    private WebAppContext contextFromWebXml() throws Exception {


        WebAppContext context = new WebAppContext();
        context.setDescriptor(webAppPath + "/WEB-INF/web.xml");
        context.setResourceBase(webAppPath);//"../src/test/webapp");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        return context;
    }


    public void stop() throws Exception {
        server.stop();
        server.destroy();
        server = null;
    }

    public Object getContextAttribute(String attrName) {
        return context.getServletContext().getAttribute(attrName);
    }

    public static class ServletPair {
        public String path;
        public Servlet servlet;

        public ServletPair(String path, Servlet servlet) {
            this.path = path;
            this.servlet = servlet;
        }
    }

}
