package io.github.codermjlee.pojo.vo.resp.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author MJ
 */
@Getter
@Setter
public class NamePVo extends MarkPVo {
    @ApiModelProperty("名称")
    private String name;
}
