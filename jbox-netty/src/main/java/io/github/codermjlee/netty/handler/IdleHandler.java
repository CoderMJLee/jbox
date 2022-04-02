package io.github.codermjlee.netty.handler;

import io.github.codermjlee.netty.ChannelService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 空闲时间检测
 *
 * @author MJ
 */
@Slf4j
public class IdleHandler extends IdleStateHandler {
    public IdleHandler(int interval) {
        super(interval, 0, 0);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        super.channelIdle(ctx, evt);

        Channel ch = ctx.channel();
        log.info("[" + ChannelService.getIp(ch) + "]" + "空闲过长");
        ch.close();
    }
}
