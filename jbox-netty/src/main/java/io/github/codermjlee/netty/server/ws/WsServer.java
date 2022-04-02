package io.github.codermjlee.netty.server.ws;

import io.github.codermjlee.common.util.Jsons;
import io.github.codermjlee.common.util.Times;
import io.github.codermjlee.netty.ChannelService;
import io.github.codermjlee.netty.ChannelSession;
import io.github.codermjlee.netty.handler.IdleHandler;
import io.github.codermjlee.netty.prop.MJNettyProp;
import io.github.codermjlee.netty.server.TcpServer;
import io.github.codermjlee.pojo.dto.WsShowTextDto;
import io.github.codermjlee.web.msg.Msg;
import io.github.codermjlee.web.msg.MsgPVo;
import io.github.codermjlee.web.msg.Msgs;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocket服务器
 *
 * @author MJ
 */
@Component
@Slf4j
public class WsServer extends TcpServer {
    @Autowired
    private MJNettyProp prop;
    @Autowired
    private WsLifeHandler lifeHandler;
    @Autowired
    private WsExceptionHandler exceptionHandler;
    @Autowired
    private WsInHandler inHandler;
    @Autowired
    private ChannelService chService;
    private final ThreadLocal<String> clientIdLocal = new ThreadLocal<>();

    @Override
    protected void startError() {
        super.startError();

        // 过一会重试
        Times.delay(this::start, 5_000);
    }

    @Override
    protected void initClientChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
            .addLast(new IdleHandler((int) prop.getWs().getTimeout() / 1000))
            .addLast(lifeHandler);

        // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
        pipeline.addLast(new HttpServerCodec());
        // 以块的方式来写的处理器
        pipeline.addLast(new ChunkedWriteHandler());
        // netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
        pipeline.addLast(new HttpObjectAggregator(65535));
        // ws://localhost:9999/ws
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        pipeline
            .addLast(inHandler)
            .addLast(exceptionHandler);
    }

    @Override
    protected void initServerBootstrap(ServerBootstrap bootstrap) {
        bootstrap
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    protected void initServerChannel(NioServerSocketChannel ch) throws Exception {

    }

    @Override
    protected int getPort() {
        return prop.getWs().getPort();
    }

    public void setClientId(String clientId) {
        clientIdLocal.set(clientId);
    }

    public void removeClientId() {
        clientIdLocal.remove();
    }

    public String getClientId() {
        return clientIdLocal.get();
    }

    public <T> T raise(String msg) {
        return clientIdLocal.get() == null ? null : Msgs.raise(msg);
    }

    public <T> T raise(Msg msg) {
        return (clientIdLocal.get() == null || msg == null) ? null : msg.raise();
    }

    public void sendClient(Msg msg, Object msgData) {
        if (msg == null) return;
        send(clientIdLocal.get(), msg, msgData);
    }

    public void sendClient(MsgPVo<?> msg) {
        if (msg == null) return;
        send(clientIdLocal.get(), msg);
    }

    public void sendClientLoading(String msg) {
        if (msg == null) return;
        sendLoading(clientIdLocal.get(), msg);
    }

    public void sendClientSuccessNote(String msg) {
        if (msg == null) return;
        sendSuccessNote(clientIdLocal.get(), msg);
    }

    public void sendClientErrorNote(String msg) {
        if (msg == null) return;
        sendErrorNote(clientIdLocal.get(), msg);
    }

    public void send(String sessionId, Msg msg, Object msgData) {
        if (sessionId == null || msg == null) return;
        ChannelSession cs = chService.get(sessionId);
        if (cs == null) return;
        send(cs.getCh(), msg, msgData);
    }

    public void send(String sessionId, MsgPVo<?> msg) {
        if (sessionId == null || msg == null) return;
        ChannelSession cs = chService.get(sessionId);
        if (cs == null) return;
        send(cs.getCh(), Jsons.getString(msg));
    }

    public void sendLoading(String sessionId, String msg) {
        if (sessionId == null || msg == null) return;
        send(sessionId,
            Msgs.WS_SHOW_TEXT,
            WsShowTextDto.alloc(msg).loading());
    }

    public void sendSuccessNote(String sessionId, String msg) {
        if (sessionId == null || msg == null) return;
        send(sessionId,
            Msgs.WS_SHOW_TEXT,
            WsShowTextDto.alloc(msg).note().success());
    }

    public void sendErrorNote(String sessionId, String msg) {
        if (sessionId == null || msg == null) return;
        send(sessionId,
            Msgs.WS_SHOW_TEXT,
            WsShowTextDto.alloc(msg).note().error());
    }

    public void send(Channel ch, Msg msg, Object msgData) {
        if (ch == null || msg == null) return;
        MsgPVo<Object> pVo = new MsgPVo<>(msg.getCode(), null);
        pVo.setData(msgData);
        send(ch, pVo);
    }

    public void send(Channel ch, MsgPVo<?> msg) {
        if (ch == null || msg == null) return;
        send(ch, Jsons.getString(msg));
    }

    public void send(Msg msg, Object msgData) {
        if (msg == null) return;
        MsgPVo<Object> pVo = new MsgPVo<>(msg.getCode(), null);
        pVo.setData(msgData);
        send(pVo);
    }

    public void send(MsgPVo<?> msg) {
        if (msg == null) return;
        send(Jsons.getString(msg));
    }

    public void send(String rawText) {
        if (rawText == null) return;
        chService.getAllSessions().forEach(session -> {
            send(session.getCh(), rawText);
        });
    }

    public void send(Channel ch, String rawText) {
        if (ch == null || rawText == null) return;
        ch.writeAndFlush(new TextWebSocketFrame(rawText));
    }

    public void raiseClient(String msg) {
        if (getClientId() == null) return;
        Msgs.raise(msg);
    }

    public void raiseClient(Msg msg) {
        if (getClientId() == null) return;
        msg.raise();
    }
}
