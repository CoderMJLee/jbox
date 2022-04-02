package io.github.codermjlee.netty.server;

import io.github.codermjlee.netty.Basic;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

/**
 * 服务器基础类
 *
 * @author MJ
 */
@Slf4j
public abstract class TcpServer extends Basic {
    protected final EventLoopGroup bossGroup = new NioEventLoopGroup();
    protected final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ServerBootstrap bootstrap = new ServerBootstrap();

    public TcpServer() {
        // 基本设置
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) throws Exception {
                        TcpServer.this.initServerChannel(ch);
                    }
                })
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        TcpServer.this.initClientChannel(ch);
                    }
                });

        // 初始化
        initServerBootstrap(bootstrap);
    }

    public void start() {
        // 监听端口
        int port = getPort();
        bootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.debug("监听{}端口成功", port);
                startSuccess();
            } else {
                log.error("监听{}端口失败", port);
                startError();
            }
        });
    }

    protected void startSuccess() {}
    protected void startError() {}

    @PreDestroy
    public void stop() {
        bossGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                log.debug("bossGroup关闭成功");
            } else {
                log.error("bossGroup关闭失败");
            }
        });

        workerGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                log.debug("workerGroup关闭成功");
            } else {
                log.error("workerGroup关闭失败");
            }
        });
    }

    protected abstract int getPort();

    /**
     * 发送数据给客户端
     */
    protected void send(NioSocketChannel ch, byte[] bytes) {
        send(ch, ch.alloc().buffer().writeBytes(bytes));
    }

    /**
     * 发送数据给客户端
     */
    protected void send(NioSocketChannel ch, ByteBuf buf) {
        ch.writeAndFlush(buf);
    }

    /**
     * 初始化ServerBootstrap
     */
    protected abstract void initServerBootstrap(ServerBootstrap bootstrap);

    protected abstract void initClientChannel(NioSocketChannel ch)
            throws Exception;

    protected abstract void initServerChannel(NioServerSocketChannel ch)
            throws Exception;
}
