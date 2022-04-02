package io.github.codermjlee.netty.server.ws;

import io.github.codermjlee.netty.ChannelService;
import io.github.codermjlee.pojo.dto.WsHeartDto;
import io.github.codermjlee.web.msg.Msgs;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author MJ
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class WsInHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Autowired
    private WsServer wsServer;
    @Autowired
    private ChannelService chService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        Channel ch = ctx.channel();
        log.debug("[" + ch.id() + "]" + frame.text());

        wsServer.send(ch, Msgs.WS_HEART, new WsHeartDto(chService.get(ch).getId()));
    }
}
