package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;
import com.wixpress.fjarr.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.wixpress.fjarr.util.StringUtils.isBlank;
import static java.net.URLEncoder.encode;

/**
 * @author AlexeyR
 * @since 12/6/12 6:06 PM
 */

public class RpcInvocation {
    public static final String UTF_8 = "UTF-8";
    private final URI serviceUri;
    private final String body;
    private final MultiMap<String, String> headers = new MultiMap<String, String>();
    private final Map<String, String> queryParams = new HashMap<String, String>();
    private String contentType = "";

    public RpcInvocation(URI serviceUri, String body) {
        if (serviceUri == null || isBlank(body))
            throw new NullPointerException("ServiceUri and body must not ber null or empty");
        this.serviceUri = serviceUri;
        this.body = body;
    }


    public String getHttpMethod() {
        return "POST";
    }

    public String getCharacterEncoding() {
        return UTF_8;
    }

    public URI getServiceUri() {
        try {
            return formatServiceUri();
        } catch (UnsupportedEncodingException e) {
            // swallow
        } catch (URISyntaxException e) {
            // swallow
        }
        return serviceUri;
    }

    private URI formatServiceUri() throws UnsupportedEncodingException, URISyntaxException {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(serviceUri.getQuery())) {
            sb.append(serviceUri.getQuery());
        }
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {

            final String encodedName = encode(entry.getKey(), UTF_8);
            final String value = entry.getValue();
            final String encodedValue = value != null ? encode(value, UTF_8) : "";
            if (sb.length() > 0)
                sb.append("&");
            sb.append(encodedName);
            sb.append("=");
            sb.append(encodedValue);
        }
        return new URI(serviceUri.getScheme(), serviceUri.getUserInfo(),
                serviceUri.getHost(), serviceUri.getPort(), serviceUri.getPath(),
                sb.toString(), serviceUri.getFragment());

    }


    public MultiMap<String, String> getAllHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }


    public String getContentType() {
        return contentType;
    }

    public RpcInvocation withHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public RpcInvocation withContentType(String contentType) {
        this.contentType = contentType;
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
        if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
        if (!headers.equals(that.headers)) return false;
        if (!queryParams.equals(that.queryParams)) return false;
        if (!serviceUri.equals(that.serviceUri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceUri.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + (headers.hashCode());
        result = 31 * result + (queryParams.hashCode());
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        return result;
    }
}
