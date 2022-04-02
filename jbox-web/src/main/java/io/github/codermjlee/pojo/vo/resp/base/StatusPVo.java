package io.github.codermjlee.pojo.vo.resp.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author MJ
 */
@Getter
@Setter
public class StatusPVo extends CreateTimePVo {
    @ApiModelProperty("状态【0:正常，1:禁用】")
    private Byte status;
}
