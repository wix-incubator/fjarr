package org.wixpress.fjarr.server;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface, managing the URL mappings for {@link ServiceEndpoint} detected by {@link RpcServiceRegistry}
 * through introspection of all defined beans in the application context.
 *
 * @author Alexeyr
 * @since Jul 7, 2011
 */
public class RpcServiceHandlerMapping extends AbstractUrlHandlerMapping implements ApplicationListener<ContextRefreshedEvent>
{

    private Map<String, Class<?>> mappedHandlers = new HashMap<String, Class<?>>();



    @Autowired
    private RpcServiceRegistry registry;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        registry.onApplicationEvent(event);
        detectHandlers();
    }

    /**
     * Register all handlers found by the {@link RpcServiceRegistry}.
     * <p>The actual URL determination for a handler is per the {@link ServiceEndpoint} instance.
     *
     * @throws org.springframework.beans.BeansException
     *          if the handler couldn't be registered
     */
    protected void detectHandlers() throws BeansException
    {

        // Take any bean name that we can determine URLs for.
        for (ServiceEndpoint endpoint : registry.getServiceEndpoints())
        {

            registerHandler(endpoint.getUrl(), endpoint);

            // save url and service interface for web-based introspection
            mappedHandlers.put(endpoint.getUrl(), endpoint.getServiceInterface());
        }
    }




    public Map<String, Class<?>> getMappedHandlers()
    {
        return mappedHandlers;
    }

}
