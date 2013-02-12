package com.wixpress.fjarr.IT.util;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author alex
 * @since 1/6/13 1:29 PM
 */

public class ITSpringServer extends ITServer
{
    public ITSpringServer(Class<?> configClass)
    {
        this(DEFAULT_PORT, configClass);
    }

    public ITSpringServer(int port, Class<?> configClass)
    {
        final DispatcherServlet servlet = new DispatcherServlet();
        servlet.setContextClass(AnnotationConfigWebApplicationContext.class);
        servlet.setContextConfigLocation(configClass.getCanonicalName());

        this.port = port;
        this.servlets = new ServletPair[]{
                new ServletPair("/*", servlet)};
        this.webAppPath = null;
    }

}
