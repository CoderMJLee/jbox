package io.github.codermjlee.netty;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MJ
 */
@Component
@SuppressWarnings("unchecked")
public class ChannelService {
    /** session id作为key */
    private final Map<String, ChannelSession> CS_S = new HashMap<>();
    /** channel id作为key */
    private final Map<String, ChannelSession> CS_C = new HashMap<>();

    public static String getIp(Channel ch) {
        if (ch == null) return null;
        InetSocketAddress ip = (InetSocketAddress) ch.remoteAddress();
        return ip.getAddress().getHostAddress();
    }

    public synchronized <T extends ChannelSession> T get(String id) {
        return (T) CS_S.get(id);
    }

    public synchronized <T extends ChannelSession> T get(Channel ch) {
        if (ch == null) return null;
        return (T) CS_C.get(ch.id().asLongText());
    }

    public boolean isOnline(String id) {
        return get(id) != null;
    }

    public boolean isOnline(Channel ch) {
        return get(ch) != null;
    }

    public synchronized Collection<ChannelSession> getAllSessions() {
        return CS_S.values();
    }

    /**
     *  关闭通道，断开连接
     */
    public synchronized <T extends ChannelSession> T remove(Channel ch) {
        if (ch == null) return null;
        // 通道id
        String id = ch.id().asLongText();
        ChannelSession cs = CS_C.remove(id);
        // 已经被移除掉了
        if (cs == null) return (T) cs;
        cs = CS_S.remove(cs.getId());
        cs.close();
        return (T) cs;
    }

    /**
     *  关闭通道，断开连接
     */
    public synchronized <T extends ChannelSession> T remove(String id) {
        if (id == null) return null;
        ChannelSession cs = CS_S.remove(id);
        // 已经被移除掉了
        if (cs == null) return (T) cs;
        cs = CS_C.remove(cs.getChId());
        cs.close();
        return (T) cs;
    }

    public synchronized <T extends ChannelSession> T add(ChannelSession session) {
        CS_S.put(session.getId(), session);
        CS_C.put(session.getChId(), session);
        return (T) session;
    }
}
