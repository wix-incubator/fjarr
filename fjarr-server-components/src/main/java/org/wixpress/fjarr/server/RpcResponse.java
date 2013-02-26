package org.wixpress.fjarr.server;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author AlexeyR
 * @since 11/29/12 5:30 PM
 */

public interface RpcResponse
{
    OutputStream getOutputStream() throws IOException;

    void setContentType(String responseContentType);

    void setStatusCode(int statusCode);
}
