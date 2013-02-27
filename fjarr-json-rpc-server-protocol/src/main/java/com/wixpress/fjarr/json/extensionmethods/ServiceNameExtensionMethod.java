package com.wixpress.fjarr.json.extensionmethods;

import com.wixpress.fjarr.json.JsonRpcExtensionMethod;

/**
 * @author alex
 * @since 1/16/13 2:17 PM
 */

public class ServiceNameExtensionMethod implements JsonRpcExtensionMethod
{

    private final String serviceClassName;

    public ServiceNameExtensionMethod(Class serviceClass)
    {
        serviceClassName = serviceClass.getName();
    }

    @Override
    public Object invoke()
    {
        return serviceClassName;
    }

    @Override
    public String getMethodName()
    {
        return "rpc.getServiceName";
    }
}
