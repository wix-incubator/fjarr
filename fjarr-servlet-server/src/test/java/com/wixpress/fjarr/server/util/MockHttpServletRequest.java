package com.wixpress.fjarr.server.util;

import com.wixpress.fjarr.util.MultiMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.wixpress.fjarr.server.util.MockUtils.fromIterator;

/**
 * @author AlexeyR
 * @since 12/6/12 12:51 PM
 */

public class MockHttpServletRequest implements HttpServletRequest
{
    final MultiMap<String, String> headers;
    //final String body;
    final String method;
    private final ServletInputStream servletInputStream;
    private Enumeration<String> headerNamesEnumeration;
    private Map<String, Enumeration<String>> headersEnumerationsCache = new HashMap<String, Enumeration<String>>();

    public MockHttpServletRequest(String method, String body, MultiMap<String, String> headers)
    {
        this.method = method;
        this.headers = headers;
        final ByteArrayInputStream request = new ByteArrayInputStream(body.getBytes(Charset.forName("UTF8")));
        servletInputStream = new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return request.read();
            }
        };
        headerNamesEnumeration = fromIterator(headers.keySet().iterator());

    }


    @Override
    public String getAuthType()
    {
        return null; 
    }

    @Override
    public Cookie[] getCookies()
    {
        return new Cookie[0]; 
    }

    @Override
    public long getDateHeader(String name)
    {
        return Long.parseLong(headers.get(name));
    }

    @Override
    public String getHeader(String name)
    {
        return headers.get(name);
    }

    @Override
    public Enumeration getHeaders(String name)
    {
        if (headersEnumerationsCache.containsKey(name))
            return headersEnumerationsCache.get(name);

        Enumeration<String> headersEnumeration = fromIterator(headers.getAll(name).iterator());
        headersEnumerationsCache.put(name, headersEnumeration);

        return headersEnumeration;
    }

    @Override
    public Enumeration getHeaderNames()
    {

        return headerNamesEnumeration;
    }

    @Override
    public int getIntHeader(String name)
    {
        return Integer.parseInt(headers.get(name));
    }

    @Override
    public String getMethod()
    {
        return method;
    }

    @Override
    public String getPathInfo()
    {
        return null; 
    }

    @Override
    public String getPathTranslated()
    {
        return null; 
    }

    @Override
    public String getContextPath()
    {
        return null; 
    }

    @Override
    public String getQueryString()
    {
        return null; 
    }

    @Override
    public String getRemoteUser()
    {
        return null; 
    }

    @Override
    public boolean isUserInRole(String role)
    {
        return false; 
    }

    @Override
    public Principal getUserPrincipal()
    {
        return null; 
    }

    @Override
    public String getRequestedSessionId()
    {
        return null; 
    }

    @Override
    public String getRequestURI()
    {
        return null; 
    }

    @Override
    public StringBuffer getRequestURL()
    {
        return null; 
    }

    @Override
    public String getServletPath()
    {
        return null; 
    }

    @Override
    public HttpSession getSession(boolean create)
    {
        return null; 
    }

    @Override
    public HttpSession getSession()
    {
        return null; 
    }

    @Override
    public boolean isRequestedSessionIdValid()
    {
        return false; 
    }

    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        return false; 
    }

    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        return false; 
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isRequestedSessionIdFromUrl()
    {
        return false; 
    }

    @Override
    public Object getAttribute(String name)
    {
        return null; 
    }

    @Override
    public Enumeration getAttributeNames()
    {
        return null; 
    }

    @Override
    public String getCharacterEncoding()
    {
        return "UTF8";
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException
    {
       
    }

    @Override
    public int getContentLength()
    {
        return 0; 
    }

    @Override
    public String getContentType()
    {
        String ret = headers.get("Content-Type");
        if (ret == null || ret.equals(""))
            ret = headers.get("content-type");
        return ret;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {

        return servletInputStream;
    }

    @Override
    public String getParameter(String name)
    {
        return null; 
    }

    @Override
    public Enumeration getParameterNames()
    {
        return null; 
    }

    @Override
    public String[] getParameterValues(String name)
    {
        return new String[0]; 
    }

    @Override
    public Map getParameterMap()
    {
        return null; 
    }

    @Override
    public String getProtocol()
    {
        return null; 
    }

    @Override
    public String getScheme()
    {
        return null; 
    }

    @Override
    public String getServerName()
    {
        return null; 
    }

    @Override
    public int getServerPort()
    {
        return 0; 
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return null; 
    }

    @Override
    public String getRemoteAddr()
    {
        return null; 
    }

    @Override
    public String getRemoteHost()
    {
        return null; 
    }

    @Override
    public void setAttribute(String name, Object o)
    {
       
    }

    @Override
    public void removeAttribute(String name)
    {
       
    }

    @Override
    public Locale getLocale()
    {
        return null; 
    }

    @Override
    public Enumeration getLocales()
    {
        return null; 
    }

    @Override
    public boolean isSecure()
    {
        return false; 
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
        return null; 
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getRealPath(String path)
    {
        return null; 
    }

    @Override
    public int getRemotePort()
    {
        return 0; 
    }

    @Override
    public String getLocalName()
    {
        return null; 
    }

    @Override
    public String getLocalAddr()
    {
        return null; 
    }

    @Override
    public int getLocalPort()
    {
        return 0; 
    }


    public static Builder post()
    {
        return new Builder("POST");
    }

    public static Builder get()
       {
           return new Builder("GET");
       }


    public static class Builder
    {
        private String method;
        private String body = "";
        private MultiMap<String, String> headers = new MultiMap<String, String>();

        private Builder(String method)
        {
            this.method = method;
        }

        public MockHttpServletRequest build()
        {
            if (method == null)
                throw new RuntimeException("Missing request method");
            return new MockHttpServletRequest(method, body, headers);
        }

        public Builder withHeader(String key, String value)
        {
            headers.put(key, value);
            return this;
        }


        public Builder withBody(String body)
        {
            this.body = body;
            return this;
        }

    }
}
