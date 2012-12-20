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
    private final HttpServletResponse response;

    public RpcServletResponse(HttpServletResponse response)
    {
        this.response = response;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return response.getOutputStream();
    }

    public void setContentType(String responseContentType)
    {
        response.setContentType(responseContentType);
    }
}
