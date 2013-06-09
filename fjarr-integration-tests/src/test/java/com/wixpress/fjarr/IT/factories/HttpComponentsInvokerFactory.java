package com.wixpress.fjarr.it.factories;

import com.wixpress.fjarr.client.ApacheHttpClient4Factory;
import com.wixpress.fjarr.client.HttpClientConfig;
import com.wixpress.fjarr.client.HttpComponentsInvoker;

/**
 * @author: ittaiz
 * @since: 5/29/13
 */
public class HttpComponentsInvokerFactory {

    public static HttpComponentsInvoker aDefaultHttpComponentsInvoker() {
        return anHttpComponentsInvokerFrom(
                HttpClientConfig.defaults());
    }

    public static HttpComponentsInvoker anHttpComponentsInvokerFrom(HttpClientConfig config) {
        return new HttpComponentsInvoker(
                new ApacheHttpClient4Factory(config));
    }

}
