package org.wixpress.fjarr.client.exceptions;

/**
 * @author shaiyallin
 * @since 1/3/12
 */
public class RpcClientInitializationException extends RuntimeException {

    public RpcClientInitializationException() {
    }

    public RpcClientInitializationException(String message) {
        super(message);
    }

    public RpcClientInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcClientInitializationException(Throwable cause) {
        super(cause);
    }
}
