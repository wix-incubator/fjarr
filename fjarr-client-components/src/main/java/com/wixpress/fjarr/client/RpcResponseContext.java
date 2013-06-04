package com.wixpress.fjarr.client;

import com.wixpress.fjarr.util.MultiMap;

import java.util.Set;

/**
 * @author alexeyr
 * @since 7/5/11 10:55 AM
 */

public class RpcResponseContext {
    private final Throwable thrown;
    private final boolean error;
    private final RpcInvocationResponse response;
    private final long requestDurationMillis;

    public RpcResponseContext(Throwable throwable, RpcInvocationResponse response, long requestDurationMillis) {
        this(throwable, response, requestDurationMillis, true);
    }

    public RpcResponseContext(RpcInvocationResponse response, long requestDurationMillis) {
        this(null, response, requestDurationMillis, false);
    }

    private RpcResponseContext(Throwable throwable, RpcInvocationResponse response,
                               long requestDurationMillis, boolean error) {
        this.thrown = throwable;
        this.response = (response != null ? response : new RpcInvocationResponse(-1, "", "", new MultiMap<String, String>()));
        this.requestDurationMillis = requestDurationMillis;
        this.error = error;

    }

    public Throwable getThrown() {
        return thrown;
    }

    public boolean isError() {
        return error;
    }

    public Set<String> getHeaders(String name) {
        return response.getAllHeaders().getAll(name);
    }

    public String getHeader(String name) {
        return response.getAllHeaders().get(name);
    }

    public long getRequestDurationMillis() {
        return requestDurationMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcResponseContext that = (RpcResponseContext) o;

        if (error != that.error) return false;
        if (requestDurationMillis != that.requestDurationMillis) return false;
        if (response != null ? !response.equals(that.response) : that.response != null) return false;
        if (thrown != null ? !thrown.equals(that.thrown) : that.thrown != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = thrown != null ? thrown.hashCode() : 0;
        result = 31 * result + (error ? 1 : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (int) (requestDurationMillis ^ (requestDurationMillis >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "RpcResponseContext{" +
                "thrown=" + thrown +
                ", error=" + error +
                ", response=" + response +
                ", requestDurationMillis=" + requestDurationMillis +
                '}';
    }
}
