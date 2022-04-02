package io.github.codermjlee.pojo.vo.req.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SearchQVo {
    @ApiModelProperty("id")
    private Serializable id;

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("排序字段")
    private String sortColumn;

    @ApiModelProperty("是否降序")
    private boolean desc;
}
