package com.wixpress.fjarr.server;

import org.springframework.util.StringUtils;

/**
 * @author alexeyr
 * @since 7/5/11 3:51 PM
 */
public class ServiceEndpoint {
    private final String name;
    private final Class<?> serviceInterface;
    private final Object serviceImplementation;
    private RpcRequestLifecycleEventHandler[] eventHandlers;
    private final String url;

    public ServiceEndpoint(Class<?> serviceInterface, Object serviceImplementation) {
        this(null, serviceInterface, serviceImplementation, new RpcRequestLifecycleEventHandler[0]);
    }

    public ServiceEndpoint(Class<?> serviceInterface, Object serviceImplementation, RpcRequestLifecycleEventHandler... eventHandlers) {
        this(null, serviceInterface, serviceImplementation, eventHandlers);

    }

    public ServiceEndpoint(String name, Class<?> serviceInterface, Object serviceImplementation) {
        this(name, serviceInterface, serviceImplementation, new RpcRequestLifecycleEventHandler[0]);
    }

    public ServiceEndpoint(String name, Class<?> serviceInterface, Object serviceImplementation, RpcRequestLifecycleEventHandler... eventHandlers) {
        this.name = name;
        this.serviceInterface = serviceInterface;
        this.serviceImplementation = serviceImplementation;
        this.eventHandlers = eventHandlers;
        this.url = resolveUrlMapping();
    }

    public String getName() {
        return name;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public Object getServiceImplementation() {
        return serviceImplementation;
    }

    public String getUrl() {
        return url;
    }

    public RpcRequestLifecycleEventHandler[] getEventHandlers() {
        return eventHandlers;
    }

    private String resolveUrlMapping() {
        String serviceName;
        if (StringUtils.hasText(getName()))
            serviceName = getName();
        else
            serviceName = getServiceInterface().getSimpleName();

        return formatUrl(serviceName);
    }

    private String formatUrl(String serviceName) {
        if (serviceName.startsWith("/"))
            return serviceName;
        else
            return "/" + serviceName;
    }

}
