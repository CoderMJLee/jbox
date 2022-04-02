package io.github.codermjlee.service.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.codermjlee.common.util.Classes;
import io.github.codermjlee.mapper.mp.MpLambdaQueryWrapper;
import io.github.codermjlee.pojo.IdObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author MJ
 */
@Transactional
public abstract class BaseServiceImpl<Mapper extends BaseMapper<Po>, Po extends IdObject>
    extends ServiceImpl<Mapper, Po> {
    @Override
    protected Class<Mapper> currentMapperClass() {
        return Classes.getGenericTypes(getClass())[0];
    }

    @Override
    protected Class<Po> currentModelClass() {
        return Classes.getGenericTypes(getClass())[1];
    }

    @Override
    public boolean update(Po entity, Wrapper<Po> updateWrapper) {
        if (entity == null && updateWrapper == null) return false;
        boolean ret = false;
        try {
            Boolean innerRet = beforeSaveUpdate(entity);
            if (innerRet != null) {
                return innerRet;
            }
            ret = super.update(entity, updateWrapper);
        } catch (Exception e) {
            throw e;
        } finally {
            afterSaveUpdate(entity, true, ret);
        }
        return ret;
    }

    @Override
    public boolean save(Po entity) {
        if (entity == null) return false;
        boolean ret = false;
        try {
            Boolean innerRet = beforeSaveUpdate(entity);
            if (innerRet != null) {
                return innerRet;
            }
            ret = super.save(entity);
        } catch (Exception e) {
            throw e;
        } finally {
            afterSaveUpdate(entity, false, ret);
        }
        return ret;
    }

    @Override
    public boolean updateById(Po entity) {
        if (entity == null) return false;
        boolean ret = false;
        try {
            Boolean innerRet = beforeSaveUpdate(entity);
            if (innerRet != null) {
                return innerRet;
            }
            ret = super.updateById(entity);
        } catch (Exception e) {
            throw e;
        } finally {
            afterSaveUpdate(entity, true, ret);
        }
        return ret;
    }

    /**
     * null表示继续往下走
     * true表示不继续往下走，返回true
     * false表示不继续往下走，返回false
     */
    protected Boolean beforeSaveUpdate(Po entity) {
        return null;
    }

    protected void afterSaveUpdate(Po entity, boolean update, boolean success) {
    }

    public Po getOneV2(MpLambdaQueryWrapper<Po> wrapper) {
        Page<Po> page = new Page<>();
        // 只查1条
        page.setSize(1);
        List<Po> records = baseMapper.selectPage(page, wrapper).getRecords();
        return CollectionUtils.isEmpty(records) ? null : records.get(0);
    }
}
