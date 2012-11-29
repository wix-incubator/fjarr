package com.wixpress.hoopoe.rpc.server.exceptions;

/**
 * @author AlexeyR
 * @since 6/12/11 9:14 AM
 */

/**
 * This exception should only be thrown at a rpc service over-http-exporter, when it's accessed using an unsupported content-type
 * (for example if the exporter only supports application/xml requests, it'll throw this exception when it encounters any other)
 * This exception MUST generate a responce with 415 Unsupported media type
 */
public class UnsupportedContentTypeException extends Exception
{
    private final String supportedContentType;

    public UnsupportedContentTypeException(String supportedContentType)
    {

        this.supportedContentType = supportedContentType;
    }

    public String getSupportedContentType()
    {
        return supportedContentType;
    }
}
