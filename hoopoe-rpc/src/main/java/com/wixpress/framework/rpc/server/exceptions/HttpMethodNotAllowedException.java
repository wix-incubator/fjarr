package com.wixpress.framework.rpc.server.exceptions;

import java.util.List;

/**
 * @author AlexeyR
 * @since 6/9/11 4:08 PM
 */

/**
 * This exception should only be thrown at a rpc service over-http-exporter, when it's accessed using an unsupported HTTP resolvedMethod
 * (for example if the exporter only supports POST requests, it'll throw this exception when it encounters a GET request)
 * This exception MUST generate a responce with 405 - Method not allowed status code.
 */
public class HttpMethodNotAllowedException extends Exception
{

    private final List<String> allowedMethods;

    public HttpMethodNotAllowedException(List<String> allowedMethods)
    {

        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedMethods()
    {
        return allowedMethods;
    }
}
