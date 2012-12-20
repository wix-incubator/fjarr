package com.wixpress.framework.rpc.client;

import com.wixpress.fjarr.monads.Pair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author alexeyr
 * @since 7/4/11 5:39 PM
 */

public interface RpcOverHttpRequestContext
{

    String getMethod();

    URI getURI();

    public void setURI(java.net.URI uri);

    public List<Pair<String, String>> getAllHeaders();

    public List<Pair<String, String>> getHeaders(String name);

    public String getHeader(String name);

    void addHeader(String name, String value);

    void setHeader(String name, String value);

    boolean containsHeader(String name);

    void addUrlParam(String name, String value) throws URISyntaxException;

    void setUrlParam(String name, String value) throws URISyntaxException;

    public String getMethodName();

    public String getServiceClass();

    public String getWire();

}
