package com.wixpress.framework.rpc.client;

import com.wixpress.framework.rpc.client.exceptions.RpcInvocationException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * @author alexeyr
 * @since 6/30/11 5:44 PM
 */

public interface RpcProtocolClient
{
    void writeRequest(Writer writer, String methodName, Object[] arguments) throws IOException;

    Object readResponse(Type returnType, Class<?>[] exceptionTypes, Reader reader) throws IOException, RpcInvocationException;

    String getContentType();

    String getAcceptType();
}
