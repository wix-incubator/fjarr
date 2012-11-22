package com.wixpress.framework.rpc.spring;

import com.wixpress.framework.rpc.client.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author alexeyr
 * @since 7/5/11 11:59 AM
 */

public class RpcOverHttpProxyFactoryBean<T> extends UrlBasedRemoteAccessor implements InitializingBean, FactoryBean<T>, BeanFactoryAware {
    RpcProtocolClient protocol;
    RpcOverHttpClientEventHandler eventHandler;

    T proxy;

    private BeanFactory beanFactory;
    private HttpClientConfig httpClientConfig = HttpClientConfig.defaults();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        if (eventHandler == null) {
            eventHandler = tryFetchEventHandler();
        }
        proxy = (T) RpcOverHttpClientProxy.create(
                getServiceInterface(),
                getServiceUrl(),
                protocol,
                httpClientConfig,
                eventHandler
        );
    }

    private RpcOverHttpClientEventHandler tryFetchEventHandler() {
        try {
            return beanFactory.getBean(RpcOverHttpClientEventHandler.class);
        } catch (BeansException e) {
            return null;
        }
    }

    @Override
    public T getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setProtocol(RpcProtocolClient protocol) {
        this.protocol = protocol;
    }

    public void setEventHandler(RpcOverHttpClientEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public void setHttpClientConfig(HttpClientConfig httpClientConfig) {
        checkNotNull(httpClientConfig);

        this.httpClientConfig = httpClientConfig;
    }
}
