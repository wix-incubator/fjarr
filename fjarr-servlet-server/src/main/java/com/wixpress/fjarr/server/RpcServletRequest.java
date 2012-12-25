package com.wixpress.fjarr.server;


import com.wixpress.fjarr.util.IOUtils;
import com.wixpress.fjarr.util.MultiMap;
import com.wixpress.fjarr.util.ReadOnlyMultiMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlexeyR
 * @since 11/29/12 1:13 PM
 */

/**
 * Adapter between HttpServletRequest and RpcRequest
 */
public class RpcServletRequest implements RpcRequest
{
    private final HttpServletRequest baseRequest;
    private final String rawRequestBody;
    private final MultiMap<String, String> headers;
    private final Map<String, RpcCookie> cookies;


    public RpcServletRequest(HttpServletRequest request) throws IOException
    {
        this.baseRequest = request;
        rawRequestBody = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        headers = convertHeaders(request);
        cookies = new HashMap<String, RpcCookie>();
        if (baseRequest.getCookies() != null)
        {
            for (Cookie c : baseRequest.getCookies())
            {
                cookies.put(c.getName(), new CookieAdapter(c));
            }
        }


    }

    public String getRawRequestBody()
    {
        return rawRequestBody;
    }

    public ReadOnlyMultiMap<String, String> getAllHeaders()
    {
        return headers;
    }

    public Collection<String> getHeaders(String headerName)
    {
        return headers.getAll(headerName);
    }

    public String getFirstHeader(String headerName)
    {
        return headers.get(headerName);
    }

    public Map<String, String> getAllQueryParameters()
    {
        return baseRequest.getParameterMap();
    }

    public String getQueryParameter(String paramName)
    {
        return baseRequest.getParameter(paramName);
    }

    public Map<String, RpcCookie> getAllCookies()
    {
        return cookies;
    }

    public RpcCookie getCookie(String cookieName)
    {
        return cookies.get(cookieName);
    }

    public String getContentType()
    {
        return baseRequest.getContentType();
    }

    public String getHttpMethod()
    {
        return baseRequest.getMethod();
    }

    private MultiMap<String, String> convertHeaders(HttpServletRequest request)
    {
        MultiMap<String, String> mmap = new MultiMap<String, String>();

        while (request.getHeaderNames().hasMoreElements())
        {
            String headerName = (String) request.getHeaderNames().nextElement();
            Enumeration enumeration = request.getHeaders(headerName);
            while (enumeration.hasMoreElements())
            {
                mmap.put(headerName, (String) enumeration.nextElement());
            }
        }
        return mmap;
    }


    private static class CookieAdapter implements RpcCookie
    {
        final Cookie cookie;

        public CookieAdapter(Cookie cookie)
        {
            this.cookie = cookie;

        }

        public String getValue()
        {
            return cookie.getValue();

        }

        public String getComment()
        {
            return cookie.getComment();
        }

        public String getDomain()
        {
            return cookie.getDomain();
        }

        public int getMaxAge()
        {
            return cookie.getMaxAge();
        }

        public String getName()
        {
            return cookie.getName();
        }

        public String getPath()
        {
            return cookie.getPath();
        }

        public boolean getSecure()
        {
            return cookie.getSecure();
        }

    }
}
