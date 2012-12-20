package com.wixpress.fjarr.client;

import com.wixpress.fjarr.client.exceptions.RpcTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;

import static java.lang.String.format;

/**
 * @author alexeyr
 * @since 6/29/11 4:16 PM
 */

public class RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
//    private static final BasicHeader CONNECTION_CLOSE_HEADER = new BasicHeader("Connection", "close");

    private URI serviceUrl;

    private final RpcClientProtocol protocol;

    public RpcClientEventHandler eventHandler;
    private RpcInvoker invoker;

    public RpcClient(URI serviceUrl, RpcClientProtocol protocol, RpcInvoker invoker) {
        this(serviceUrl, protocol, invoker, null);
    }


    public RpcClient(URI serviceUrl, RpcClientProtocol protocol, RpcInvoker invoker, RpcClientEventHandler eventHandler) {
        if (serviceUrl == null || protocol == null || invoker == null)
            throw new NullPointerException();

        LOGGER.info(format("Configuring RpcClient [serviceUrl='%s',protocol=%s, invoker=%s]", serviceUrl, protocol, invoker));

        this.serviceUrl = serviceUrl;
        this.protocol = protocol;
        this.invoker = invoker;
        this.eventHandler = eventHandler;
    }



    public <T> T invoke(Method method, Object... arguments) throws Throwable {
        return (T)_invoke(method.getDeclaringClass().getName(), method.getName(), method.getGenericReturnType(), arguments);
    }


    public <T> T invoke(String serviceName, String methodName, Class<T> returnType, Object... arguments) throws Throwable {
        return returnType.cast(_invoke(serviceName, methodName, returnType, arguments));

    }


    private Object _invoke(String serviceName, String methodName, Type returnType, Object... arguments) throws Throwable {
        StringWriter stringWriter = new StringWriter();
        // write request
        String invocationBody = protocol.writeRequest( methodName, arguments);
        RpcInvocation invocation = new RpcInvocation(serviceUrl, invocationBody);
        final RpcRequestContext requestContext = new RpcRequestContext(invoker, invocation, serviceName,
                methodName, invocationBody);

        // fire preInvoke event
        if (eventHandler != null)
            eventHandler.preInvoke(requestContext);

        RpcInvocationResponse response = null;
        Object result = null;
        long startTimeMillis = System.currentTimeMillis();
        try {
            try {
                response = invoker.invoke(invocation);
            } catch (Exception e) {
                invoker.abort(invocation);

                long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;

                throw new RpcTransportException(format("Exception while communicating with server [endpoint=%s,timeSpentMillis=%d,invoker=%s]",
                        getEndPointDescription(serviceName, methodName), timeSpentMillis, invoker), e);
            }


            if (response.getStatusCode() != 200) {
                invoker.abort(invocation);
                long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;

                throw new RpcTransportException(format("Unexpected server HTTP status code [endpoint=%s,httpStatusLine='%s',timeSpentMillis=%d,invoker=%s]",
                        getEndPointDescription(serviceName, methodName), formatStatusLine(response), timeSpentMillis, invoker), response.getStatusCode());
            }
            // read the response

            long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;
            final String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().equals("")) {
                invoker.abort(invocation);
                throw new RpcTransportException(format("Empty HTTP response [endpoint=%s,httpStatusLine='%s',timeSpentMillis=%d,invoker=%s]",
                        getEndPointDescription(serviceName, methodName), formatStatusLine(response), timeSpentMillis, invoker), response.getStatusCode());
            }

            try {
                result = protocol.readResponse(returnType, responseBody);

                // fire postInvoke event
                if (eventHandler != null)
                    eventHandler.postInvoke(requestContext, new RpcResponseContext(result, response, timeSpentMillis));
            } catch (Exception e) {
                invoker.abort(invocation);
                throw e;
            }
        } catch (Exception e) {
            long timeSpentMillis = System.currentTimeMillis() - startTimeMillis;
            // fire postInvoke event
            if (eventHandler != null)
                eventHandler.postInvoke(requestContext, new RpcResponseContext(e, response, timeSpentMillis));

            throw e;
        }

        return result;
    }

    private String formatStatusLine(RpcInvocationResponse response) {
        return format("%s %s", response.getStatusCode(), response.getStatusDescription());
    }


    private String getEndPointDescription(String serviceName, String methodName) {
        return format("{url='%s',service='%s',method='%s'}",
                serviceUrl,
                serviceName,
                methodName);
    }

    public URI getServiceUrl() {
        return serviceUrl;
    }
}
