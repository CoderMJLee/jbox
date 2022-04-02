package io.github.codermjlee.netty;

import io.netty.channel.Channel;

/**
 * @author MJ
 */
public class WsSession extends ChannelSession {
    public WsSession(Channel ch) {
        super(ch);
    }

    @Override
    public void close() {

    }
}
