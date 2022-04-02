package io.github.codermjlee.netty.server.ws;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author MJ
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class WsExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[" + ctx.channel().id() + "]" + "异常", cause);

        super.exceptionCaught(ctx, cause);
    }
}
