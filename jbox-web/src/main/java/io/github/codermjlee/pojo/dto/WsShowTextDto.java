package io.github.codermjlee.pojo.dto;

import lombok.Getter;

/**
 * @author MJ
 */
@Getter
public class WsShowTextDto {
    private byte type;
    private byte level;
    private String text;

    private WsShowTextDto() {}

    public static WsShowTextDto alloc() {
        return new WsShowTextDto();
    }

    public static WsShowTextDto alloc(String text) {
        WsShowTextDto dto = alloc();
        dto.text = text;
        return dto;
    }

    public WsShowTextDto setType(byte type) {
        this.type = type;
        return this;
    }

    public WsShowTextDto setLevel(byte level) {
        this.level = level;
        return this;
    }

    public WsShowTextDto setText(String text) {
        this.text = text;
        return this;
    }

    public WsShowTextDto info() {
        return setLevel(Level.INFO);
    }

    public WsShowTextDto success() {
        return setLevel(Level.SUCCESS);
    }

    public WsShowTextDto warning() {
        return setLevel(Level.WARNING);
    }

    public WsShowTextDto error() {
        return setLevel(Level.ERROR);
    }

    public WsShowTextDto msg() {
        return setType(Type.MSG);
    }

    public WsShowTextDto note() {
        return setType(Type.NOTE);
    }

    public WsShowTextDto loading() {
        return setType(Type.LOADING);
    }

    public interface Type {
        // 中间消息显示
        byte MSG = 0;
        // 通知
        byte NOTE = 1;
        // 加载中...
        byte LOADING = 2;
    }

    public interface Level {
        byte INFO = 0;
        byte SUCCESS = 1;
        byte WARNING = 2;
        byte ERROR = 3;
    }
}
