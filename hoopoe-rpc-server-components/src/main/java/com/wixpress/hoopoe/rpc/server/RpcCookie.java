package com.wixpress.hoopoe.rpc.server;

/**
 * @author AlexeyR
 * @since 11/29/12 1:08 PM
 */

public interface RpcCookie
{
    String getValue();
    String getComment();
    String getDomain();
    int getMaxAge();
    String getName();
    String getPath();
    boolean getSecure();
}
