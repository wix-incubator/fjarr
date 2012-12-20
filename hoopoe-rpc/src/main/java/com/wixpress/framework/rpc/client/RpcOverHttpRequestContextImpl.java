package com.wixpress.framework.rpc.client;

import com.wixpress.fjarr.monads.Pair;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author alexeyr
 * @since 7/4/11 6:13 PM
 */

public class RpcOverHttpRequestContextImpl implements RpcOverHttpRequestContext
{
    private final HttpRequestBase httpRequest;
    private String serviceClass;
    private final String methodName;
    private final String wire;

    RpcOverHttpRequestContextImpl(HttpRequestBase httpRequest, String serviceClass, String methodName, String wire)
    {
        this.httpRequest = httpRequest;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.wire = wire;
    }

    @Override
    public String getMethod()
    {
        return httpRequest.getMethod();
    }


    @Override
    public URI getURI()
    {
        return httpRequest.getURI();
    }

    @Override
    public void setURI(URI uri)
    {
        httpRequest.setURI(uri);
    }

    @Override
    public List<Pair<String, String>> getAllHeaders()
    {
        return convertHeaders(httpRequest.getAllHeaders());
    }

    @Override
    public List<Pair<String, String>> getHeaders(String name)
    {
        return convertHeaders(httpRequest.getHeaders(name));
    }

    private List<Pair<String, String>> convertHeaders(Header[] headers)
    {
        List<Pair<String, String>> hdrs = new ArrayList<Pair<String, String>>();
        for (Header h : headers)
        {
            hdrs.add(new Pair<String, String>(h.getName(), h.getValue()));
        }
        return hdrs;
    }

    @Override
    public String getHeader(String name)
    {
        return httpRequest.getFirstHeader(name).getValue();
    }

    @Override
    public void addHeader(String name, String value)
    {
        httpRequest.addHeader(name, value);
    }

    @Override
    public void setHeader(String name, String value)
    {
        httpRequest.setHeader(name, value);
    }

    @Override
    public boolean containsHeader(String name)
    {
        return httpRequest.containsHeader(name);
    }

    @Override
    public void addUrlParam(String name, String value) throws URISyntaxException
    {
        URI origUri = httpRequest.getURI();
        List<NameValuePair> qparams = URLEncodedUtils.parse(origUri, "UTF-8");
        qparams.add(new BasicNameValuePair(name, value));

        URI uri = URIUtils.createURI(origUri.getScheme(), origUri.getHost(), origUri.getPort(), origUri.getPath(), URLEncodedUtils.format(qparams, "UTF-8"), origUri.getFragment());
        httpRequest.setURI(uri);
    }

    @Override
    public void setUrlParam(String name, String value) throws URISyntaxException
    {
        URI origUri = httpRequest.getURI();
        List<NameValuePair> qparams = URLEncodedUtils.parse(origUri, "UTF-8");
        for (NameValuePair nvp : qparams)
        {
            if (nvp.getName().equals(name))
                qparams.remove(nvp);
        }

        qparams.add(new BasicNameValuePair(name, value));

        URI uri = URIUtils.createURI(origUri.getScheme(), origUri.getHost(), origUri.getPort(), origUri.getPath(), URLEncodedUtils.format(qparams, "UTF-8"), origUri.getFragment());
        httpRequest.setURI(uri);
    }

    public HttpRequestBase getHttpRequest() {
        return httpRequest;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getWire() {
        return wire;
    }

    public String getServiceClass()
    {
        return serviceClass;
    }
}
