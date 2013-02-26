package org.wixpress.fjarr.client;

import org.jboss.netty.channel.Channel;

/**
 * Created by evg.
 * Date: 12/12/11
 * Time: 01:29
 */
public interface HttpResponseCompletedListener
{

    public void responseCompleted(Channel channel);
}
