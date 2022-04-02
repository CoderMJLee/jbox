package io.github.codermjlee.pojo.vo.resp.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel("分页数据")
@Getter
@Setter
public class PageListPVo<T> {
    @ApiModelProperty("总记录数")
    private Long total;

    @ApiModelProperty("当前页码")
    private Long pageNo;

    @ApiModelProperty("一页的数量")
    private Long pageSize;

    @ApiModelProperty("总页数")
    private Long pages;

    @ApiModelProperty("一页的数据")
    private List<T> data;
}
