package com.wixpress.fjarr.server;

/**
 * @author alexeyr
 * @since 7/5/11 3:51 PM
 */
public class ServiceEndpoint
{
    private String name;
    private Class<?> serviceInterface;
    private Object serviceImplementation;
    private RpcRequestLifecycleEventHandler[] eventHandlers;
    private String url;

    public ServiceEndpoint(Class<?> serviceInterface, Object serviceImplementation)
    {
        this(null, serviceInterface, serviceImplementation, new RpcRequestLifecycleEventHandler[0]);
    }

    public ServiceEndpoint(Class<?> serviceInterface, Object serviceImplementation, RpcRequestLifecycleEventHandler... eventHandlers)
    {
        this(null, serviceInterface, serviceImplementation, eventHandlers);

    }

    public ServiceEndpoint(String name, Class<?> serviceInterface, Object serviceImplementation)
    {
        this(name, serviceInterface, serviceImplementation, new RpcRequestLifecycleEventHandler[0]);
    }

    public ServiceEndpoint(String name, Class<?> serviceInterface, Object serviceImplementation, RpcRequestLifecycleEventHandler... eventHandlers)
    {
        this.name = name;
        this.serviceInterface = serviceInterface;
        this.serviceImplementation = serviceImplementation;
        this.eventHandlers = eventHandlers;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Class<?> getServiceInterface()
    {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface)
    {
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceImplementation()
    {
        return serviceImplementation;
    }

    public void setServiceImplementation(Object serviceImplementation)
    {
        this.serviceImplementation = serviceImplementation;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public RpcRequestLifecycleEventHandler[] getEventHandlers()
    {
        return eventHandlers;
    }

    public void setEventHandlers(RpcRequestLifecycleEventHandler[] eventHandlers)
    {
        this.eventHandlers = eventHandlers;
    }
}
