package com.wixpress.fjarr.json;

import com.wixpress.fjarr.server.RpcResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 12/19/12
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockRpcResponse implements RpcResponse {
    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private String responseContentType;


    @Override
    public OutputStream getOutputStream() throws IOException {
        return stream;
    }

    @Override
    public void setContentType(String responseContentType) {

        this.responseContentType = responseContentType;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public String responseContent() throws UnsupportedEncodingException {
        return stream.toString("UTF8");
    }
}
