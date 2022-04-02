package io.github.codermjlee.pojo.vo.resp.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author MJ
 */
@Getter
@Setter
public class MarkPVo extends CreateTimePVo {
    @ApiModelProperty("备注")
    private String mark;
}
