package io.github.codermjlee.pojo.vo.req.page;

import io.github.codermjlee.pojo.base.StatusPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * @author MJ
 */
@Getter
@Setter
public class StatusPageQVo extends PageQVo {
    @Range(min = StatusPo.Status.MIN, max = StatusPo.Status.MAX)
    @ApiModelProperty("状态【0:正常，1:禁用】")
    private Byte status;
}
