package io.github.codermjlee.web.msg;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Msg extends RuntimeException {
    /** 状态码 */
    private int status;
    /** 编号 */
    private int code;
    /** 消息的类型 */
    private Byte type;

    public Msg(int status, int code, String msg) {
        this(status, code, msg, null, null);
    }

    public Msg(int status, int code, String msg, Throwable throwable) {
        this(status, code, msg, null, throwable);
    }

    public Msg(int status, int code, String msg, Byte type) {
        this(status, code, msg, type, null);
    }

    public Msg(int status, int code, String msg, Byte type, Throwable throwable) {
        super(msg, throwable);

        this.status = status;
        this.code = code;
        this.type = type;
    }

    public <T> T raise() throws Msg {
        throw this;
    }

    public MsgPVo<?> getPVo() {
        MsgPVo<?> pVo = new MsgPVo<>(code, getMessage());
        pVo.setMsgType(type);
        return pVo;
    }
}
