package io.github.codermjlee.pojo.vo.req.base;

import io.github.codermjlee.pojo.base.StatusPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StatusSaveQVo {
    @NotNull
    @Range(min = StatusPo.Status.MIN, max = StatusPo.Status.MAX)
    @ApiModelProperty(value = "状态【0:正常，1:禁用】", required = true)
    private Byte status;
}
