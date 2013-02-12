package com.wixpress.fjarr.client;

import com.wixpress.fjarr.http.ContentTypeUtils;
import com.wixpress.fjarr.util.MultiMap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


/**
 * @author evg
 */

class HttpResponseHandler extends SimpleChannelUpstreamHandler
{

    private HttpResponseCompletedListener responseCompletedListener;


    public HttpResponseHandler(HttpResponseCompletedListener responseCompletedListener)
    {
        this.responseCompletedListener = responseCompletedListener;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
    {

        HttpResponse response = (HttpResponse) e.getMessage();
        RequestResponse requestResponse = (RequestResponse) ctx.getAttachment();
        try
        {


            final String contentTypeHeader = response.getHeader(HttpHeaders.Names.CONTENT_TYPE);
            String charset = ContentTypeUtils.extractCharSet(contentTypeHeader, requestResponse.invocation.getCharacterEncoding());


            ChannelBuffer content = response.getContent();
            String body = null;
            if (content.readable())
            {

                byte[] resultContent = new byte[content.capacity()];
                content.getBytes(0, resultContent);
                body = new String(resultContent, Charset.forName(charset));
            }


            RpcInvocationResponse result = new RpcInvocationResponse(response.getStatus().getCode(),
                    response.getStatus().getReasonPhrase(), body,
                    extractHeaders(response.getHeaders()));

            requestResponse.responseFuture.setValue(result);

        }
        catch (Exception ex)
        {
            requestResponse.responseFuture.setError(ex);
        }
        finally
        {
            responseCompletedListener.responseCompleted(ctx.getChannel());

        }

    }

    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
    {

        RequestResponse requestResponse = (RequestResponse) ctx.getAttachment();
        if (requestResponse != null)
        {
            requestResponse.responseFuture.setError(e.getCause());

            responseCompletedListener.responseCompleted(ctx.getChannel());
        }
    }

    private MultiMap<String, String> extractHeaders(List<Map.Entry<String, String>> headers)
    {

        MultiMap<String, String> mmHeaders = new MultiMap<String, String>();

        for (Map.Entry<String, String> entry : headers)
        {
            mmHeaders.put(entry.getKey(), entry.getValue());
        }
        return mmHeaders;
    }
}

