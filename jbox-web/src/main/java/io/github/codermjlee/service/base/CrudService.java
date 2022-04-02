package io.github.codermjlee.service.base;

import io.github.codermjlee.pojo.vo.req.page.PageQVo;

/**
 * @author MJ
 */
public interface CrudService
        <Po, P_QVo extends PageQVo, P_PVo, S_QVo, U_QVo>
        extends PageService<Po, P_QVo, P_PVo> {
    /*
     * 返回id
     */
    Object saveQVo(S_QVo qVo);

    boolean updateQVo(U_QVo qVo);

    void truncate();
}
