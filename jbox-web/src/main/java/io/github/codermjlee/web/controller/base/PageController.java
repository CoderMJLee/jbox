package io.github.codermjlee.web.controller.base;

import io.github.codermjlee.mapper.map.O2os;
import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.pojo.vo.resp.page.PageListPVo;
import io.github.codermjlee.service.base.PageService;
import io.github.codermjlee.web.msg.MsgPVo;
import io.github.codermjlee.web.msg.Msgs;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.Valid;
import java.util.List;

public abstract class PageController
    <Service extends PageService<Po, P_QVo, P_PVo>
        , Po, P_QVo extends PageQVo, P_PVo>
    extends BaseController<Service> {
    @GetMapping("/list")
    @ApiOperation("获取所有")
    public MsgPVo<List<P_PVo>> list() {
        return process(() -> O2os.os2os(service.list()), Msgs.LIST_ERROR);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public MsgPVo<PageListPVo<P_PVo>> page(@Valid P_QVo qVo) {
        return process(() -> service.page(qVo), Msgs.LIST_ERROR);
    }
}
