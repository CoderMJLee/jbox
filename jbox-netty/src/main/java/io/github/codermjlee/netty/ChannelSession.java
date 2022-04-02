package io.github.codermjlee.netty;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 一个通道会话
 *
 * @author MJ
 */
@Setter
@Getter
public abstract class ChannelSession {
    protected Channel ch;
    /**
     * Channel的id（asLongText）
     */
    protected String chId;
    /**
     * 会话id
     */
    protected String id;

    public ChannelSession(Channel ch, String id) {
        if (ch == null) return;
        chId = ch.id().asLongText();
        this.ch = ch;
        this.id = id == null ? chId : id;
    }

    public ChannelSession(Channel ch) {
        this(ch, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelSession that = (ChannelSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract void close();
}
