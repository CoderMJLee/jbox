package io.github.codermjlee.service.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.github.codermjlee.mapper.base.SqlMapper;
import io.github.codermjlee.mapper.map.O2os;
import io.github.codermjlee.pojo.IdObject;
import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * MP传入wrapper查询返回的集合，不会为null
 *
 * @author MJ
 */
@Transactional
public abstract class CrudServiceImpl
    <Mapper extends BaseMapper<Po>, Po extends IdObject, P_QVo extends PageQVo, P_PVo, S_QVo, U_QVo>
    extends PageServiceImpl<Mapper, Po, P_QVo, P_PVo>
    implements CrudService<Po, P_QVo, P_PVo, S_QVo, U_QVo> {
    /*
     * null表示继续往下走
     * true表示不继续往下走，返回true
     * false表示不继续往下走，返回false
     */
    protected Boolean beforeSave(S_QVo qVo) {
        return null;
    }

    /*
     * null表示继续往下走
     * true表示不继续往下走，返回true
     * false表示不继续往下走，返回false
     */
    protected Boolean beforeUpdate(U_QVo qVo) {
        return null;
    }

    @Override
    public Object saveQVo(S_QVo qVo) {
        if (qVo == null) return null;
        Boolean ret = beforeSave(qVo);
        if (ret != null) return ret;
        Po po = O2os.o2o(qVo);
        save(po);
        return po.getId();
    }

    @Override
    public boolean updateQVo(U_QVo qVo) {
        if (qVo == null) return false;
        Boolean ret = beforeUpdate(qVo);
        if (ret != null) return ret;
        return updateById(O2os.o2o(qVo));
    }

    @Override
    public void truncate() {
        ((SqlMapper<Po>) baseMapper).execute(
            "truncate table "
                + SqlHelper.table(getEntityClass()).getTableName()
        );
    }
}
