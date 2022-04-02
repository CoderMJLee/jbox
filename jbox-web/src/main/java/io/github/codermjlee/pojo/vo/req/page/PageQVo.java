package io.github.codermjlee.pojo.vo.req.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQVo extends SearchQVo {
    @ApiModelProperty("当前页码")
    private long pageNo;

    @ApiModelProperty("一页的数量")
    private long pageSize;
}
