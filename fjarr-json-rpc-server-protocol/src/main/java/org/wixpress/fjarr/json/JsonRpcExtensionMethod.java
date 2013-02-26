package org.wixpress.fjarr.json;

/**
 * @author alex
 * @since 1/16/13 1:25 PM
 */

public interface JsonRpcExtensionMethod
{
    Object invoke();
    String getMethodName();
}
