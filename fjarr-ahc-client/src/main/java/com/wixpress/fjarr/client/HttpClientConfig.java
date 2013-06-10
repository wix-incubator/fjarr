package com.wixpress.fjarr.client;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 3/6/12
 */
public class HttpClientConfig {
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 2 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 1 * 1000;
    public static final int DEFAULT_POOL_CONNECTION_TTL_MILLIS = 5 * 1000;
    public static final int DEFAULT_POOL_MAX_TOTAL = 100;
    public static final int DEFAULT_POOL_DEFAULT_MAX_PER_HOST = 100;
    public static final boolean DEFAULT_USE_CONNECTION_POOL = false;

    /**
     * Timeout in milliseconds for successful TCP connection to establish
     */
    private int connectionTimeoutMillis = DEFAULT_CONNECTION_TIMEOUT_MILLIS;

    /**
     * Socket read timeout (SO_TIMEOUT)
     */
    private int socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT_MILLIS;

    /**
     * How long opened connection remains alive in pool (not relevant since all our RPC traffic
     * doesn't make any reuse for connections (EugOlshein asked not to do it)
     */
    private int poolConnectionTTLMillis = DEFAULT_POOL_CONNECTION_TTL_MILLIS;

    /**
     * Max number of total concurrent open connections in pool regardles to route
     */
    private int poolMaxTotal = DEFAULT_POOL_MAX_TOTAL;

    /**
     * Max number of concurrent open connections per route (host).
     * Since every RPC service client proxy comes with its own connection manager
     * <code>poolDefaultMaxPerHost</code> is the actual the pool site.
     */
    private int poolDefaultMaxPerHost = DEFAULT_POOL_DEFAULT_MAX_PER_HOST;

    /**
     * Defines whether to use connection pooling (reusing connections and working with HTTP version 1.1
     */
    private boolean useConnectionPool = DEFAULT_USE_CONNECTION_POOL;

    private HttpClientConfig() {

    }

    public static HttpClientConfig defaults() {
        return new HttpClientConfig();
    }

    public HttpClientConfig withConnectionTimeoutMillis(int connectionTimeoutMillis) {
        setConnectionTimeoutMillis(connectionTimeoutMillis);
        return this;
    }

    public HttpClientConfig withSocketTimeoutMillis(int socketTimeoutMillis) {
        setSocketTimeoutMillis(socketTimeoutMillis);
        return this;
    }

    public HttpClientConfig withPoolConnectionTTLMillis(int poolConnectionTTLSeconds) {
        setPoolConnectionTTLMillis(poolConnectionTTLSeconds);
        return this;
    }

    public HttpClientConfig withPoolMaxTotal(int poolMaxTotal) {
        setPoolMaxTotal(poolMaxTotal);
        return this;
    }

    public HttpClientConfig withPoolDefaultMaxPerHost(int poolMaxPerHost) {
        setPoolDefaultMaxPerHost(poolMaxPerHost);
        return this;
    }

    public HttpClientConfig withUseConnectionPool(boolean useConnectionPool) {
        setUseConnectionPool(useConnectionPool);
        return this;
    }

    public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }

    public void setSocketTimeoutMillis(int socketTimeoutMillis) {
        this.socketTimeoutMillis = socketTimeoutMillis;
    }

    public void setPoolConnectionTTLMillis(int poolConnectionTTLMillis) {
        this.poolConnectionTTLMillis = poolConnectionTTLMillis;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    public void setPoolDefaultMaxPerHost(int poolDefaultMaxPerHost) {
        this.poolDefaultMaxPerHost = poolDefaultMaxPerHost;
    }

    public void setUseConnectionPool(boolean useConnectionPool) {
        this.useConnectionPool = useConnectionPool;
    }

    public int getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    public int getSocketTimeoutMillis() {
        return socketTimeoutMillis;
    }

    public int getPoolConnectionTTLMillis() {
        return poolConnectionTTLMillis;
    }

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public int getPoolDefaultMaxPerHost() {
        return poolDefaultMaxPerHost;
    }

    public boolean isUseConnectionPool() {
        return useConnectionPool;
    }

    @Override
    public String toString() {
        return "HttpClientConfig{" +
                "connectionTimeoutMillis=" + connectionTimeoutMillis +
                ", socketTimeoutMillis=" + socketTimeoutMillis +
                ", poolConnectionTTLMillis=" + poolConnectionTTLMillis +
                ", poolMaxTotal=" + poolMaxTotal +
                ", poolDefaultMaxPerHost=" + poolDefaultMaxPerHost +
                ", useConnectionPool=" + useConnectionPool +
                '}';
    }
}
