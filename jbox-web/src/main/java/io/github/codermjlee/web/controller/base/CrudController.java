package io.github.codermjlee.web.controller.base;

import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.service.base.CrudService;
import io.github.codermjlee.web.msg.MsgPVo;
import io.github.codermjlee.web.msg.Msgs;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public abstract class CrudController
    <Service extends CrudService<Po, P_QVo, P_PVo, S_QVo, U_QVo>
        , Po, P_QVo extends PageQVo, P_PVo, S_QVo, U_QVo>
    extends PageController<Service, Po, P_QVo, P_PVo> {
    @PostMapping("/save")
    @ApiOperation("添加")
    public MsgPVo<?> save(@Valid @RequestBody S_QVo saveQVo) {
        return process(() -> service.saveQVo(saveQVo) != null, Msgs.SAVE_ERROR);
    }

    @PostMapping("/update")
    @ApiOperation("更新")
    public MsgPVo<?> update(@Valid @RequestBody U_QVo updateQVo) {
        return process(() -> service.updateQVo(updateQVo), Msgs.UPDATE_ERROR);
    }

    @PostMapping("/remove")
    @ApiOperation("删除")
    public MsgPVo<?> remove(
        @ApiParam("id数组") @NotEmpty @Valid
        @RequestBody List<Serializable> ids
    ) {
        return process(() -> service.removeByIds(ids), Msgs.REMOVE_ERROR);
    }
}
