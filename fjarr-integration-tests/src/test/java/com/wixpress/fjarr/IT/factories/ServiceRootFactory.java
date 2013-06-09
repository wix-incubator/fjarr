package com.wixpress.fjarr.it.factories;

/**
 * @author: ittaiz
 * @since: 6/9/13
 */
public class ServiceRootFactory {
    public static String aServiceRootFor(Class<?> serviceClass, int serverPort) {
        return String.format("http://127.0.0.1:%d/%s", serverPort, serviceClass.getSimpleName());
    }
}
