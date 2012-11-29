package com.wixpress.hoopoe.rpc.server;

/**
 * @author alexeyr
 * @since 10/25/12 11:52 AM
 */

public class RpcRequestStatistics
{
    private long requestProcessingFinishedTimestamp = 0;
    private long requestParsingFinishedTimestamp = 0;
    private long requestErrorTimestamp = 0;
    private final long requestStartTimestamp;

    public RpcRequestStatistics(long requestStartTimestamp)
    {
        this.requestStartTimestamp = requestStartTimestamp;
    }

    public long getRequestStartTimestamp()
    {
        return requestStartTimestamp;
    }

    public long getRequestParsingFinishedTimestamp()
    {
        return requestParsingFinishedTimestamp;
    }

    public void setRequestParsingFinishedTimestamp(long requestParsingFinishedTimestamp)
    {
        this.requestParsingFinishedTimestamp = requestParsingFinishedTimestamp;
    }

    public long getRequestProcessingFinishedTimestamp()
    {
        return requestProcessingFinishedTimestamp;
    }

    public void setRequestProcessingFinishedTimestamp(long requestProcessingFinishedTimestamp)
    {
        this.requestProcessingFinishedTimestamp = requestProcessingFinishedTimestamp;
    }

    public long getRequestErrorTimestamp()
    {
        return requestErrorTimestamp;
    }

    public void setRequestErrorTimestamp(long requestErrorTimestamp)
    {
        this.requestErrorTimestamp = requestErrorTimestamp;
    }
}
