package io.github.codermjlee.pojo.vo.resp.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author MJ
 */
@Getter
@Setter
public class CreateTimePVo {
    @ApiModelProperty("创建时间")
    private Date createTime;
}
