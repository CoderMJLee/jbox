package io.github.codermjlee.web.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel("返回结果")
@Getter
@Setter
@ToString
public class MsgPVo<T> {
    @ApiModelProperty("编号【0代表成功】")
    private Integer code = 0;

    @ApiModelProperty("消息")
    private String msg;

    @ApiModelProperty("消息的显示类型")
    private Byte msgType;

    @ApiModelProperty("数据")
    private T data;

    public MsgPVo() {}

    public MsgPVo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MsgPVo(T data) {
        this.data = data;
    }

    public interface MsgType {
        byte SILENT = 0;
        byte WARN = 1;
        byte ERROR = 2;
        byte NOTIFICATION = 4;
        byte PAGE = 9;
    }
}
