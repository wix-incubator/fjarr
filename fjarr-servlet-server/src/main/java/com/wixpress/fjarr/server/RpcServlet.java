package com.wixpress.fjarr.server;

import com.wixpress.fjarr.server.exceptions.BadRequestException;
import com.wixpress.fjarr.server.exceptions.GenericRpcException;
import com.wixpress.fjarr.server.exceptions.HttpMethodNotAllowedException;
import com.wixpress.fjarr.server.exceptions.UnsupportedContentTypeException;
import com.wixpress.fjarr.util.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author AlexeyR
 * @since 11/25/12 12:29 PM
 */

/**
 * Implements an Fjarr-Rpc Server as a servlet
 */
public class RpcServlet extends HttpServlet
{

    public static final String FJARR_PROTOCOL_ATTRIBUTE = "__FjarrProtocol";
    public static final String FJARR_SERVER_ATTRIBUTE = "__FjarrServer";
    RpcServer server;

    public RpcServlet()
    {

    }

    public RpcServlet(RpcServer server)
    {
        this.server = server;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        RpcServletRequest request = new RpcServletRequest(req);
        RpcResponse response = new RpcServletResponse(resp);
        try
        {
            server.handleRequest(request, response);
        }
        catch (HttpMethodNotAllowedException e)
        {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            resp.setHeader("Allow", StringUtils.collectionToDelimitedString(e.getAllowedMethods(), ", "));
        }
        catch (UnsupportedContentTypeException e)
        {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        catch (BadRequestException e)
        {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getOutputStream().print(e.getMessage());
        }
        catch (GenericRpcException e)
        {
            try
            {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                // No body
            }
            catch (Throwable t)
            {
                // do nothing since the stream is dead
            }
        }


    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        if (server != null)
            return;

        String serviceClassName = config.getInitParameter("serviceInterface");
        String serviceImplementationClassName = config.getInitParameter("serviceImplementationClass");
        String protocolClassName = config.getInitParameter("protocolClass");

        boolean protocolFromContext = false;
        if (StringUtils.isBlank(protocolClassName))
        {
            protocolClassName = config.getServletContext().getInitParameter("protocolClass");
            protocolFromContext = true;
        }

        if (serviceClassName == null || serviceImplementationClassName == null || protocolClassName == null)
            throw new ServletException("Missing configuration. \"serviceInterface\"," +
                    "\"serviceImplementationClass\" and \"protocolClass\" must be specified");


        Class serviceClass;
        Object serviceImpl;
        RpcProtocol protocol;
        try
        {
            serviceClass = Class.forName(serviceClassName);
            if (!serviceClass.isInterface())
                throw new ServletException("Service Interface must be an interface");
            serviceImpl = Class.forName(serviceImplementationClassName).newInstance();
            if (!serviceClass.isInstance(serviceImpl))
                throw new ServletException("Service Implementation and Service Interface don't match");


            if (!protocolFromContext)
                protocol = (RpcProtocol) (Class.forName(protocolClassName).newInstance());
            else
            {
                // try to get protocol from servlet context - maybe it was allready initialized
                protocol = (RpcProtocol) config.getServletContext().getAttribute(FJARR_PROTOCOL_ATTRIBUTE);
                if (protocol == null)
                {
                    protocol = (RpcProtocol) (Class.forName(protocolClassName).newInstance());
                }
            }

            if (protocol != null) // set the protocol to the servlet context from access and reuse;
                config.getServletContext().setAttribute(FJARR_PROTOCOL_ATTRIBUTE,protocol);

        }
        catch (ClassNotFoundException e)
        {
            throw new ServletException("Failed loading class", e);
        }
        catch (InstantiationException e)
        {
            throw new ServletException("Service Implementation has to have parameter-less param", e);
        }
        catch (IllegalAccessException e)
        {
            throw new ServletException("Failed instantiating Service Implementation", e);
        }

        server = new RpcServer(protocol, serviceImpl, serviceClass);
        config.getServletContext().setAttribute(FJARR_SERVER_ATTRIBUTE +config.getServletName(),server);
    }
}
