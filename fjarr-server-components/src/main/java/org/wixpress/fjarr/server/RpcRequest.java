package org.wixpress.fjarr.server;

import org.wixpress.fjarr.util.ReadOnlyMultiMap;

import java.util.Collection;
import java.util.Map;

/**
 * @author AlexeyR
 * @since 11/29/12 11:08 AM
 */

public interface RpcRequest
{
    String getRawRequestBody();

    ReadOnlyMultiMap<String, String> getAllHeaders();

    Collection<String> getHeaders(String headerName);

    String getFirstHeader(String headerName);

    Map<String, String> getAllQueryParameters();

    String getQueryParameter(String paramName);

    Map<String, RpcCookie> getAllCookies();

    public RpcCookie getCookie(String cookieName);

    String getContentType();

    String getHttpMethod();
}
