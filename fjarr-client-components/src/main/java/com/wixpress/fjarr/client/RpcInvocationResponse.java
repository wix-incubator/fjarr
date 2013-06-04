package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;
import com.wixpress.fjarr.util.ReadOnlyMultiMap;

/**
 * @author AlexeyR
 * @since 12/6/12 6:35 PM
 */

public class RpcInvocationResponse {
    private final int statusCode;
    private final String statusDescription;
    private final String body;
    private final MultiMap<String, String> headers = new MultiMap<String, String>();

    public RpcInvocationResponse(int statusCode, String statusDescription, String body, MultiMap<String, String> headers) {
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
        this.body = body;
        this.headers.putAll(headers);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public String getBody() {
        return body;
    }

    public ReadOnlyMultiMap<String, String> getAllHeaders() {
        return headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcInvocationResponse that = (RpcInvocationResponse) o;

        if (statusCode != that.statusCode) return false;
        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        if (headers != null ? !headers.equals(that.headers) : that.headers != null) return false;
        if (statusDescription != null ? !statusDescription.equals(that.statusDescription) : that.statusDescription != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = statusCode;
        result = 31 * result + (statusDescription != null ? statusDescription.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RpcInvocationResponse{" +
                "statusCode=" + statusCode +
                ", statusDescription='" + statusDescription + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + headers +
                '}';
    }
}
