package com.wixpress.fjarr.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alexeyr
 * @since 7/5/11 2:58 PM
 */

public abstract class RpcServiceRegistration
{

    public abstract void registerServices();

    public List<ServiceEndpoint> endpoints = new ArrayList<ServiceEndpoint>();

    public void registerEndpoint(Class<?> serviceInterface, Object serviceImplementation)
    {
        endpoints.add(new ServiceEndpoint(serviceInterface, serviceImplementation));
    }

    public void registerEndpoint(Class<?> serviceInterface, Object serviceImplementation, RpcRequestLifecycleEventHandler... eventHandlers)
    {
        endpoints.add(new ServiceEndpoint(serviceInterface, serviceImplementation, eventHandlers));
    }

    public void registerEndpoint(Class<?> serviceInterface, Object serviceImplementation, String serviceName)
    {
        endpoints.add(new ServiceEndpoint(serviceName, serviceInterface, serviceImplementation));
    }

    public void registerEndpoint(Class<?> serviceInterface, Object serviceImplementation, String serviceName, RpcRequestLifecycleEventHandler... eventHandlers)
    {
        endpoints.add(new ServiceEndpoint(serviceName, serviceInterface, serviceImplementation, eventHandlers));
    }

    public List<ServiceEndpoint> getEndpoints()
    {
        return endpoints;
    }

}
