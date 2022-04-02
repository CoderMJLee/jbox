package io.github.codermjlee.netty.server.ws;

import io.github.codermjlee.netty.ChannelService;
import io.github.codermjlee.netty.WsSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author MJ
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WsLifeHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ChannelService chService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        log.debug("[" + ch.id() + "]" + "连接");
        WsSession session = new WsSession(ch);
        chService.add(session);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        log.debug("[" + ch.id() + "]" + "离线");
        chService.remove(ch);

        super.channelInactive(ctx);
    }

    @Getter
    @Setter
    private static class ClientId {
        private String clientId;
        public ClientId(String clientId) {
            this.clientId = clientId;
        }
    }
}
