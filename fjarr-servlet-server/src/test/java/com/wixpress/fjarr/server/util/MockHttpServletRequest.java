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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Cookie[] getCookies()
    {
        return new Cookie[0];  //To change body of implemented methods use File | Settings | File Templates.
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPathTranslated()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getContextPath()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getQueryString()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRemoteUser()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isUserInRole(String role)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Principal getUserPrincipal()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRequestedSessionId()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRequestURI()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StringBuffer getRequestURL()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getServletPath()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HttpSession getSession(boolean create)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HttpSession getSession()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRequestedSessionIdValid()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute(String name)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getAttributeNames()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCharacterEncoding()
    {
        return "UTF8";
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getContentLength()
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getParameterNames()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterValues(String name)
    {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map getParameterMap()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProtocol()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getScheme()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getServerName()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getServerPort()
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRemoteAddr()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRemoteHost()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAttribute(String name, Object o)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAttribute(String name)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Locale getLocale()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getLocales()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSecure()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRealPath(String path)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getRemotePort()
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getLocalName()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getLocalAddr()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getLocalPort()
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
