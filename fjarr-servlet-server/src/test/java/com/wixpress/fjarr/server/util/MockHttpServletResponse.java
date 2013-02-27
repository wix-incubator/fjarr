package com.wixpress.fjarr.server.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
* @author AlexeyR
* @since 12/6/12 1:15 PM
*/
public class MockHttpServletResponse implements HttpServletResponse
{

    int status = 200;
    private String statusMessage;

    ServletOutputStream outputStream;
    final ByteArrayOutputStream baseStream = new ByteArrayOutputStream();

    public MockHttpServletResponse()
    {

        outputStream = new ServletOutputStream(){

            @Override
            public void write(int b) throws IOException
            {
                baseStream.write(b);
            }
        };
    }

    @Override
    public void addCookie(Cookie cookie)
    {
       
    }

    @Override
    public boolean containsHeader(String name)
    {
        return false; 
    }

    @Override
    public String encodeURL(String url)
    {
        return null; 
    }

    @Override
    public String encodeRedirectURL(String url)
    {
        return null; 
    }

    @Override
    @SuppressWarnings("deprecation")
    public String encodeUrl(String url)
    {
        return null; 
    }

    @Override
    @SuppressWarnings("deprecation")
    public String encodeRedirectUrl(String url)
    {
        return null; 
    }

    @Override
    public void sendError(int sc, String msg) throws IOException
    {
       
    }

    @Override
    public void sendError(int sc) throws IOException
    {
       
    }

    @Override
    public void sendRedirect(String location) throws IOException
    {
       
    }

    @Override
    public void setDateHeader(String name, long date)
    {
       
    }

    @Override
    public void addDateHeader(String name, long date)
    {
       
    }

    @Override
    public void setHeader(String name, String value)
    {
       
    }

    @Override
    public void addHeader(String name, String value)
    {
       
    }

    @Override
    public void setIntHeader(String name, int value)
    {
       
    }

    @Override
    public void addIntHeader(String name, int value)
    {
       
    }

    @Override
    public void setStatus(int sc)
    {
        this.status = sc;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setStatus(int sc, String sm)
    {
        this.status = sc;
        this.statusMessage = sm;
    }

    @Override
    public String getCharacterEncoding()
    {
        return null; 
    }

    @Override
    public String getContentType()
    {
        return null; 
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        return null; 
    }

    @Override
    public void setCharacterEncoding(String charset)
    {
       
    }

    @Override
    public void setContentLength(int len)
    {
       
    }

    @Override
    public void setContentType(String type)
    {
       
    }

    @Override
    public void setBufferSize(int size)
    {
       
    }

    @Override
    public int getBufferSize()
    {
        return 0; 
    }

    @Override
    public void flushBuffer() throws IOException
    {
       
    }

    @Override
    public void resetBuffer()
    {
       
    }

    @Override
    public boolean isCommitted()
    {
        return false; 
    }

    @Override
    public void reset()
    {
       
    }

    @Override
    public void setLocale(Locale loc)
    {
       
    }

    @Override
    public Locale getLocale()
    {
        return null; 
    }

    public int getStatusCode()
    {
        return this.status;
    }

    public String getBody() throws UnsupportedEncodingException
    {
        return baseStream.toString("UTF8");
    }
}
