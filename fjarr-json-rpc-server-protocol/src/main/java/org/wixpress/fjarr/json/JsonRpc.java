package org.wixpress.fjarr.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.wixpress.fjarr.json.extensionmethods.ServiceNameExtensionMethod;
import org.wixpress.fjarr.server.RpcServer;

/**
 * @author alex
 * @since 2/6/13 10:45 AM
 */

public class JsonRpc
{
    public static <T> RpcServer server(Class<T> serviceClass, T serviceImplementation)
    {
        return server(serviceClass, serviceImplementation, new ObjectMapper());
    }

    public static <T> RpcServer server(Class<T> serviceClass, T serviceImplementation, ObjectMapper mapper)
    {
        return new RpcServer(
                new JsonRpcProtocol(
                        mapper),
                serviceImplementation,
                serviceClass,
                new JsonRpcExtensionMethodExecutor(
                        new ServiceNameExtensionMethod(serviceClass)
                ));
    }
}
