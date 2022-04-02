package io.github.codermjlee.web.controller.base;

import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.service.base.CrudService;
import io.github.codermjlee.web.msg.MsgPVo;
import io.github.codermjlee.web.msg.Msgs;

import javax.validation.Valid;

public abstract class FormCrudController
    <Service extends CrudService<Po, P_QVo, P_PVo, S_QVo, U_QVo>
        , Po, P_QVo extends PageQVo, P_PVo, S_QVo, U_QVo>
    extends CrudController<Service, Po, P_QVo, P_PVo, S_QVo, U_QVo> {
    public MsgPVo<?> save(@Valid S_QVo saveQVo) {
        return process(() -> service.saveQVo(saveQVo) != null, Msgs.SAVE_ERROR);
    }

    public MsgPVo<?> update(@Valid U_QVo updateQVo) {
        return process(() -> service.updateQVo(updateQVo), Msgs.UPDATE_ERROR);
    }
}
