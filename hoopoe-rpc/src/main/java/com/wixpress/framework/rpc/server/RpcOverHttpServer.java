package com.wixpress.framework.rpc.server;


import com.google.common.collect.ImmutableMultimap;
import com.wixpress.framework.rpc.server.exceptions.BadRequestException;
import com.wixpress.framework.rpc.server.exceptions.HttpMethodNotAllowedException;
import com.wixpress.framework.rpc.server.exceptions.MethodNotFoundException;
import com.wixpress.framework.rpc.server.exceptions.UnsupportedContentTypeException;
import com.wixpress.framework.util.ReflectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

/**
 * @author alexeyr
 * @since 6/2/11 1:59 PM
 */

public class RpcOverHttpServer
{
    private final RpcOverHttpProtocol protocol;
    private final Object serviceImpl;
    private final Class<?> serviceInterface;
    RpcRequestLifecycleEventHandler lifecycleEventHandler;

    private Logger log = LoggerFactory.getLogger(RpcOverHttpServer.class);

    public RpcOverHttpServer(RpcOverHttpProtocol protocol, Object serviceImpl, Class<?> serviceInterface)
    {
        this.protocol = protocol;
        this.serviceImpl = serviceImpl;
        this.serviceInterface = serviceInterface;
    }

    public RpcOverHttpServer(RpcOverHttpProtocol protocol, Object serviceImpl, Class<?> serviceInterface, RpcRequestLifecycleEventHandler lifecycleEventHandler)
    {
        this.protocol = protocol;
        this.serviceImpl = serviceImpl;
        this.serviceInterface = serviceInterface;
        this.lifecycleEventHandler = lifecycleEventHandler;
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        RPCServletRequestWrapper requestWrapper = new RPCServletRequestWrapper(request);
        long postProcessingTime = 0, errorTime = 0;
        RpcRequestStatistics stats = new RpcRequestStatistics(System.currentTimeMillis());

        try
        {
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRecievedRequest(requestWrapper, response);

            RpcRequest rpcRequest = parseRequest(requestWrapper);
            stats.setRequestParsingFinishedTimestamp(System.currentTimeMillis());
            rpcRequest.setStatistics(stats);
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRequestParsed(requestWrapper, response, serviceInterface, rpcRequest);

            for (RpcInvocation invocation : rpcRequest.getInvocations())
            {
                if (!invocation.isError())
                {
                    invokeMethod(requestWrapper, response, rpcRequest, invocation);
                }
            }
            stats.setRequestProcessingFinishedTimestamp(System.currentTimeMillis());
            protocol.writeResponse(response, rpcRequest);

            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWritten(requestWrapper, response, rpcRequest);

        }
        catch (HttpMethodNotAllowedException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.setHeader("Allow", StringUtils.collectionToDelimitedString(e.getAllowedMethods(), ", "));
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(requestWrapper, response, e, requestWrapper.getBody(), stats);
        }
        catch (UnsupportedContentTypeException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(requestWrapper, response, e, requestWrapper.getBody(), stats);
        }
        catch (BadRequestException e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().print(e.getMessage());
            log.error(e.getMessage(), e);
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(requestWrapper, response, e, requestWrapper.getBody(), stats);
        }
        catch (Exception e)
        {
            stats.setRequestErrorTimestamp(System.currentTimeMillis());
            try
            {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                // No body
            }
            catch (Throwable t)
            {
                // do nothing since the stream is dead
            }
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcResponseWriteError(requestWrapper, response, e, requestWrapper.getBody(), stats);
        }
    }

    private RpcRequest parseRequest(RPCServletRequestWrapper request) throws UnsupportedContentTypeException, IOException, BadRequestException, HttpMethodNotAllowedException
    {
        RpcRequest rpcRequest = protocol.parseRequest(request);
        rpcRequest.setRawRequestBody(request.getBody());

        ImmutableMultimap.Builder<String, String> params = ImmutableMultimap.builder();

        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String name = (String) parameterNames.nextElement();
            for (String value : request.getParameterValues(name))
            {
                params.put(name, value);
            }
        }

        rpcRequest.setQueryParams(params.build());

        return rpcRequest;
    }

    void invokeMethod(HttpServletRequest request, HttpServletResponse response, RpcRequest rpcRequest, RpcInvocation invocation)
    {
        try
        {
            List<Method> methods = ReflectionUtils.findMethods(serviceInterface, invocation.getMethodName());
            if (methods == null || methods.size() == 0)
            {
                invocation.setError(new MethodNotFoundException("Method [%s] was not found", invocation.getMethodName()));
                // hook for handling resolving error
                if (lifecycleEventHandler != null)
                    lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, rpcRequest, invocation);
                return;
            }
            protocol.resolveMethod(methods, invocation, rpcRequest);
            // hook for handling resolving success
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, rpcRequest, invocation);

        }
        catch (Exception e)
        {
            invocation.setError(e);
            // hook for handling resolving error
            if (lifecycleEventHandler != null)
                lifecycleEventHandler.handleRpcInvocationMethodResolved(request, response, rpcRequest, invocation);
        }
        if (!invocation.isError())
        {
            try
            {
                Object result = invocation.getResolvedMethod().invoke(serviceImpl, invocation.getResolvedParameters());
                invocation.setInvocationResult(result);
                // hook for handling invocation success
                if (lifecycleEventHandler != null)
                    lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, rpcRequest, invocation);
            }
            catch (Exception e)
            {
                if (e instanceof InvocationTargetException)
                {
                    InvocationTargetException ite = (InvocationTargetException) e;
                    if (ite.getTargetException() instanceof Exception)
                    {
                        invocation.setError((Exception) ite.getTargetException());

                    }
                    else
                    {
                        // target exception is a non-Exception Throwable, something horrible must've happened
                        invocation.setError(new RuntimeException("Fatal error encountered in RPC server", ite.getTargetException()));
                    }

                }
                else
                {
                    invocation.setError(e);
                }

                // hook for handling invocation error
                if (lifecycleEventHandler != null)
                    lifecycleEventHandler.handleRpcInvocationMethodInvoked(request, response, rpcRequest, invocation);
            }

        }

    }

    public RpcOverHttpProtocol getProtocol()
    {
        return protocol;
    }

    public Object getServiceImpl()
    {
        return serviceImpl;
    }

    public Class<?> getServiceInterface()
    {
        return serviceInterface;
    }

    private class RPCServletRequestWrapper extends HttpServletRequestWrapper
    {

        private final RPCServletInputStream stream;

        public RPCServletRequestWrapper(HttpServletRequest request) throws IOException
        {
            super(request);
            stream = new RPCServletInputStream(request.getInputStream());

        }

        @Override
        public ServletInputStream getInputStream() throws IOException
        {
            return stream;
        }

        public String getBody()
        {
            return stream.getContent();
        }
    }

    private class RPCServletInputStream extends ServletInputStream
    {
        private InputStream stream;

        private String content;


        public RPCServletInputStream(ServletInputStream stream) throws IOException
        {
            try
            {
                this.content = IOUtils.toString(stream, "UTF-8");

            }
            catch (IOException e)
            {
                this.content = "";

            }
            this.stream = IOUtils.toInputStream(this.content, "UTF-8");

        }

        @Override
        public int read() throws IOException
        {
            return stream.read();
        }

        public String getContent()
        {
            return content;
        }

    }

}
