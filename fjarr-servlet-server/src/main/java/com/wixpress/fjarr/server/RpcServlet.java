package com.wixpress.fjarr.server;

import com.wixpress.fjarr.server.exceptions.BadRequestException;
import com.wixpress.fjarr.server.exceptions.GenericRpcException;
import com.wixpress.fjarr.server.exceptions.HttpMethodNotAllowedException;
import com.wixpress.fjarr.server.exceptions.UnsupportedContentTypeException;
import com.wixpress.fjarr.util.StringUtils;

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

    RpcServer server;

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


}
