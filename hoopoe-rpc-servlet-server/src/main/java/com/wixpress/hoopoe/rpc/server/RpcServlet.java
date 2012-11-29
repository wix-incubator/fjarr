package com.wixpress.hoopoe.rpc.server;

import com.wixpress.hoopoe.rpc.server.exceptions.BadRequestException;
import com.wixpress.hoopoe.rpc.server.exceptions.GenericRpcException;
import com.wixpress.hoopoe.rpc.server.exceptions.HttpMethodNotAllowedException;
import com.wixpress.hoopoe.rpc.server.exceptions.UnsupportedContentTypeException;
import com.wixpress.hoopoe.rpc.utils.StringUtils;

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
 * Implements an Hoopoe-Rpc Server as a servlet
 */
public class RpcServlet extends HttpServlet
{

    RpcServer server;

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
