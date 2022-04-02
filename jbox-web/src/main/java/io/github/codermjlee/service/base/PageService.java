package io.github.codermjlee.service.base;

import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.pojo.vo.resp.page.PageListPVo;

/**
 * @author MJ
 */
public interface PageService
    <Po, P_QVo extends PageQVo, P_PVo>
    extends BaseService<Po> {
    PageListPVo<P_PVo> page(P_QVo qVo);
}
