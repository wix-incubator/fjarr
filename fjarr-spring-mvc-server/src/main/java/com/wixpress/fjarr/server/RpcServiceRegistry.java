package com.wixpress.fjarr.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author alexeyr
 * @since 7/6/11 9:12 AM
 */

public class RpcServiceRegistry implements ApplicationListener<ContextRefreshedEvent>
{
    private Map<String, ServiceEndpoint> serviceEndpoints = new HashMap<String, ServiceEndpoint>();
    private Logger logger = LoggerFactory.getLogger(RpcServiceRegistry.class);
    boolean isInitialized = false;




    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if (!isInitialized)
        {
            detectRegistrations(event.getApplicationContext());
            isInitialized = true;
        }
    }


    protected void detectRegistrations(ApplicationContext applicationContext) throws BeansException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Looking for URL mappings in application context: " + applicationContext);
        }
        Map<String, RpcServiceRegistration> beans = applicationContext.getBeansOfType(RpcServiceRegistration.class);

        // Take any bean name that we can determine URLs for.
        for (Map.Entry<String, RpcServiceRegistration> entry : beans.entrySet())
        {
            RpcServiceRegistration serviceRegistry = entry.getValue();
            //execute the actual registry code
            serviceRegistry.registerServices();
            registerEndpoints(serviceRegistry.getEndpoints());
        }
    }

    protected void registerEndpoints(List<ServiceEndpoint> endpoints)
    {
        for (ServiceEndpoint serviceEndpoint : endpoints)
        {
            String serviceName;
            if (StringUtils.hasText(serviceEndpoint.getName()))
                serviceName = serviceEndpoint.getName();
            else
                serviceName = serviceEndpoint.getServiceInterface().getSimpleName();

            String url = formatUrl(serviceName);
            serviceEndpoint.setUrl(url);

            // save url and service interface for web-based introspection
            this.serviceEndpoints.put(url, serviceEndpoint);
        }

    }

    private String formatUrl(String serviceName)
    {
        if (serviceName.startsWith("/"))
            return serviceName;
        else
            return "/" + serviceName;
    }

    /**
     * returns collection of endpoint which are managed by the registry
     *
     * @return
     */
    public Collection<ServiceEndpoint> getServiceEndpoints()
    {
        return serviceEndpoints.values();
    }


}
