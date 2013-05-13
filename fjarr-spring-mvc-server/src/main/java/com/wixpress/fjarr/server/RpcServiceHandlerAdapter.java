package com.wixpress.fjarr.server;

import com.wixpress.fjarr.server.exceptions.BadRequestException;
import com.wixpress.fjarr.server.exceptions.GenericRpcException;
import com.wixpress.fjarr.server.exceptions.HttpMethodNotAllowedException;
import com.wixpress.fjarr.server.exceptions.UnsupportedContentTypeException;
import com.wixpress.fjarr.util.StringUtils;
import com.wixpress.fjarr.validation.SpringValidatorRpcEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author Alexeyr
 * @since Jul 5, 2011
 *        <p/>
 *        This is a reference implementation of a Spring's HandlerAdapter wrapping an RPC server.
 *        Note - the handler adapter does not returns an error view, it just sets the response status and in some cases prints out
 *        error details to the response opdybody
 */
public class RpcServiceHandlerAdapter implements HandlerAdapter
{

    @Autowired
    protected RpcProtocol protocol;

    @Autowired(required = false)
    protected Validator validator;

    protected Map<ServiceEndpoint, RpcServer> exporters = new HashMap<ServiceEndpoint, RpcServer>();

    public boolean supports(Object handler)
    {
        return handler instanceof ServiceEndpoint;
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception
    {

        try
        {
            getServer((ServiceEndpoint) handler).handleRequest(new RpcServletRequest(request), new RpcServletResponse(response));
        }
        catch (HttpMethodNotAllowedException e)
        {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.setHeader("Allow", StringUtils.collectionToDelimitedString(e.getAllowedMethods(), ", "));
        }
        catch (UnsupportedContentTypeException e)
        {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        catch (BadRequestException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().print(e.getMessage());
        }
        catch (GenericRpcException e)
        {
            try
            {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                // No body
            }
            catch (Throwable t)
            {
                // do nothing since the stream is dead
            }
        }
        catch (IOException e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // No body
        }
        return null;
    }

    public long getLastModified(HttpServletRequest request, Object handler)
    {
        RpcServer server = getServer((ServiceEndpoint) handler);
        if (server instanceof LastModified)
        {
            return ((LastModified) server).getLastModified(request);
        }
        return -1L;
    }

    private RpcServer getServer(ServiceEndpoint endpoint)
    {
        if (exporters.containsKey(endpoint))
            return exporters.get(endpoint);
        else
        {
            List<RpcRequestLifecycleEventHandler> lifecycleEventHandlers = new ArrayList<RpcRequestLifecycleEventHandler>();
            Collections.addAll(lifecycleEventHandlers,endpoint.getEventHandlers());
            if (validator != null)
            {
                SpringValidatorRpcEventHandler vh = new SpringValidatorRpcEventHandler(validator);
                lifecycleEventHandlers.add(vh);
            }
            RpcServer server = new RpcServer(protocol, endpoint.getServiceImplementation(), endpoint.getServiceInterface(),lifecycleEventHandlers);
            exporters.put(endpoint, server);
            return server;
        }
    }

}
