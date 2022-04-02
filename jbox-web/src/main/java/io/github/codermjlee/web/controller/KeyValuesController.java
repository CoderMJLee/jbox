package io.github.codermjlee.web.controller;

import io.github.codermjlee.common.util.Classes;
import io.github.codermjlee.service.KvsService;
import io.github.codermjlee.web.controller.base.BaseController;
import io.github.codermjlee.web.msg.MsgPVo;
import io.github.codermjlee.web.msg.Msgs;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author MJ
 */
public abstract class KeyValuesController<Dto>
    extends BaseController<KvsService> {
    protected final Class<Dto> CLS = (Class<Dto>) Classes.getGenericType(getClass());
    protected final String KEY = KvsService.defaultKey(CLS);

    @PostMapping("/update")
    @ApiOperation("更新")
    public MsgPVo<?> update(@RequestBody @Valid Dto dto) {
        return process(() -> service.setJsonValue(KEY, dto),
            Msgs.OPERATE_ERROR);
    }

    @GetMapping("/get")
    @ApiOperation("获取")
    public MsgPVo<Dto> get() {
        return process(() -> service.getJsonValue(KEY, CLS),
            Msgs.OPERATE_ERROR);
    }
}
