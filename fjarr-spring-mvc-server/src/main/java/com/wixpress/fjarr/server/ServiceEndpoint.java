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
    private String url;

    public ServiceEndpoint(Class<?> serviceInterface, Object serviceImplementation)
    {
        this.serviceInterface = serviceInterface;
        this.serviceImplementation = serviceImplementation;
    }

    public ServiceEndpoint(String name, Class<?> serviceInterface, Object serviceImplementation)
    {
        this.name = name;
        this.serviceInterface = serviceInterface;
        this.serviceImplementation = serviceImplementation;
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
}
