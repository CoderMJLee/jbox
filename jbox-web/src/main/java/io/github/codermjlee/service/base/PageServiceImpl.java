package io.github.codermjlee.service.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.codermjlee.common.util.Classes;
import io.github.codermjlee.mapper.base.PageMapper;
import io.github.codermjlee.mapper.mp.MpLambdaQueryWrapper;
import io.github.codermjlee.mapper.mp.MpQueryWrapper;
import io.github.codermjlee.mapper.util.Mps;
import io.github.codermjlee.pojo.IdObject;
import io.github.codermjlee.pojo.base.LogicDeletePo;
import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.pojo.vo.resp.page.PageListPVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author MJ
 */
@SuppressWarnings("unchecked")
public abstract class PageServiceImpl
    <Mapper extends BaseMapper<Po>, Po extends IdObject, P_QVo extends PageQVo, P_PVo>
    extends BaseServiceImpl<Mapper, Po>
    implements PageService<Po, P_QVo, P_PVo> {
    protected String tblPrefix;
    private final List<String> keywordColumns = new ArrayList<>();
    protected P_QVo lastPageQVo;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageListPVo<P_PVo> page(P_QVo qVo) {
        if (qVo == null) return null;
        lastPageQVo = qVo;

        PageListPVo<P_PVo> result;
        if (baseMapper instanceof PageMapper) {
            PageMapper<Po, P_PVo> mapper = (PageMapper<Po, P_PVo>) baseMapper;
            result = Mps.page(mapper::selectPagePVos, qVo, wrapper -> {
                // 逻辑删除
                if (LogicDeletePo.class.isAssignableFrom(getEntityClass())) {
                    wrapper.eqs(LogicDeletePo.Deleted.NO, Mps.column(tblPrefix, LogicDeletePo::getDeleted));
                }
                wrapper.eqs(qVo.getId(), tblPrefix + ".id");
                page(qVo, wrapper);
            });
        } else {
            result = Mps.page(baseMapper, qVo, wrapper -> {
                page(qVo, wrapper);
            });
        }
        processPagePVoList(result.getData());
        return result;
    }

    protected void processPagePVoList(List<P_PVo> list) {}

    protected <T> void addKeywordColumns(SFunction<T, ?>... columns) {
        for (SFunction<?, ?> column : columns) {
            keywordColumns.add(column(column));
        }
    }

    protected <T> String column(SFunction<T, ?> column) {
        return Mps.column(tblPrefix, column);
    }

    protected void addKeywordColumns(String... columns) {
        Collections.addAll(keywordColumns, columns);
    }

    protected void page(P_QVo qVo, MpLambdaQueryWrapper<Po> wrapper) {
    }

    protected void page(P_QVo qVo, MpQueryWrapper<P_PVo> wrapper) {
        if (keywordColumns.isEmpty()) return;
        wrapper
            .likes(qVo.getKeyword(), keywordColumns);
    }

    protected PageListPVo<P_PVo> listByLastPageQVo() {
        P_QVo qVo = lastPageQVo;
        if (qVo == null) {
            try {
                qVo = (P_QVo) Classes.getGenericTypes(getClass())[2].newInstance();
            } catch (Throwable e) {
                return null;
            }
        }
        qVo.setPageSize(Long.MAX_VALUE);
        return page(qVo);
    }
}
