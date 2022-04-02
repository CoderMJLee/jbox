package io.github.codermjlee.pojo.vo.req.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * @author MJ
 */
@Getter
@Setter
public class MarkSaveQVo {
    @Length(max = 255)
    @ApiModelProperty("备注")
    private String mark;
}
