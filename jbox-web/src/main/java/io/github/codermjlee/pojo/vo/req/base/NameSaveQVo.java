package io.github.codermjlee.pojo.vo.req.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author MJ
 */
@Getter
@Setter
public class NameSaveQVo extends MarkSaveQVo {
    @NotBlank
    @Length(max = 15)
    @ApiModelProperty(value = "名称", required = true)
    private String name;
}
