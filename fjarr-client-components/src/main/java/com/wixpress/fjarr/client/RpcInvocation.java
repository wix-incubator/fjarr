package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlexeyR
 * @since 12/6/12 6:06 PM
 */

public class RpcInvocation {
    private final URI serviceUri;
    private final String body;
    private final MultiMap<String, String> headers = new MultiMap<String, String>();
    private final Map<String, String> queryParams = new HashMap<String, String>();

    public RpcInvocation(URI serviceUri, String body) {
        this.serviceUri = serviceUri;
        this.body = body;
    }


    public String getHttpMethod() {
        return "POST";
    }

    public URI getServiceUri() {
        return serviceUri;
    }

    public MultiMap<String, String> getAllHeaders() {
        return headers;
    }

    public RpcInvocation withHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public RpcInvocation withQueryParameter(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcInvocation that = (RpcInvocation) o;

        if (!body.equals(that.body)) return false;
        if (!serviceUri.equals(that.serviceUri)) return false;

        if (queryParams.keySet().size() != that.queryParams.keySet().size()) return false;
        for (String key : queryParams.keySet()) {
            if (!that.queryParams.containsKey(key) && that.queryParams.get(key).equals(queryParams.get(key)))
                return false;
        }
        if (!headers.equals(that.headers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceUri.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + headers.hashCode();
        result = 31 * result + queryParams.hashCode();
        return result;
    }
}
