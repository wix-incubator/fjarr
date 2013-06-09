package com.wixpress.fjarr.it.util;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author alex
 * @since 1/6/13 1:29 PM
 */

public class ITSpringServer extends ITServer {
    public ITSpringServer(Class<?> configClass) {
        this(DEFAULT_PORT, configClass);
    }

    public ITSpringServer(int port, Class<?> configClass) {
        super(port);
        final DispatcherServlet servlet = new DispatcherServlet();
        servlet.setContextClass(AnnotationConfigWebApplicationContext.class);
        servlet.setContextConfigLocation(configClass.getCanonicalName());

        this.servlets = new ServletPair[]{
                new ServletPair("/*", servlet)};
    }

}
