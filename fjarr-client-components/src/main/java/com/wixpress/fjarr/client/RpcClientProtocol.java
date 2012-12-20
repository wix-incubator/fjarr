package com.wixpress.fjarr.client;

import com.wixpress.fjarr.client.exceptions.RpcInvocationException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author alexeyr
 * @since 6/30/11 5:44 PM
 */

public interface RpcClientProtocol
{
    String writeRequest( String methodName, Object[] arguments) throws IOException;
    <T> T readResponse(Type returnType, String response) throws RpcInvocationException;

    String getContentType();

    String getAcceptType();
}
