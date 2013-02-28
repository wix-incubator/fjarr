package com.wixpress.fjarr.server;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author AlexeyR
 * @since 11/29/12 5:33 PM
 */

public class RpcServletResponse implements RpcResponse
{
    private final HttpServletResponse baseResponse;

    public RpcServletResponse(HttpServletResponse response)
    {
        this.baseResponse = response;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return baseResponse.getOutputStream();
    }

    public void setContentType(String responseContentType)
    {
        baseResponse.setContentType(responseContentType);
    }

    @Override
    public void setStatusCode(int statusCode)
    {
        baseResponse.setStatus(statusCode);
    }

    public HttpServletResponse getBaseResponse()
    {
        return baseResponse;
    }
}
