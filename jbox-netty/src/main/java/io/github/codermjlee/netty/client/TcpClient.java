package io.github.codermjlee.netty.client;

import io.github.codermjlee.common.util.binary.Bytes;
import io.github.codermjlee.netty.Basic;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MJ
 */
@Slf4j
public abstract class TcpClient extends Basic {
    protected final EventLoopGroup group = new NioEventLoopGroup();
    protected NioSocketChannel channel;
    protected int port;
    protected String host;
    private final Bootstrap bootstrap = new Bootstrap();

    public TcpClient() {
        // 基本设置
        bootstrap
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    TcpClient.this.initChannel(ch);
                }
            });

        // 初始化
        initBootstrap(bootstrap);
    }

    public NioSocketChannel getChannel() {
        return channel;
    }

    @Override
    public void start() {
        if (port < 1) return;
        if (host == null) return;

        // 连接
        bootstrap.connect(host, port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                log.info("连接服务器[{}:{}]成功", host, port);
                startSuccess();
            } else {
                log.error("连接服务器[{}:{}]失败", host, port);
                startError();
            }
        });
    }

    @Override
    public void stop() {
        group.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                log.info("bossGroup关闭成功");
                stopSuccess();
            } else {
                log.error("bossGroup关闭失败");
                stopError();
            }
        });
    }

    /**
     * 发送数据给服务器
     */
    public void send(byte[] bytes) {
        if (Bytes.empty(bytes) || channel == null) return;
        send(channel.alloc().buffer().writeBytes(bytes));
    }

    /**
     * 发送数据给服务器
     */
    public void send(ByteBuf buf) {
        if (buf == null || channel == null) return;
        channel.writeAndFlush(buf);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    protected void startSuccess() {}
    protected void startError() {}
    protected void stopSuccess() {}
    protected void stopError() {}
    protected void initBootstrap(Bootstrap bootstrap) {}

    /**
     * 初始化与服务器的通道
     */
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                channel = (NioSocketChannel) ctx.channel();

                super.channelActive(ctx);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                channel = null;

                super.channelInactive(ctx);
            }
        });
    }

}
