package io.github.codermjlee.mapper.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import io.github.codermjlee.mapper.mp.MpPage;
import org.apache.ibatis.annotations.Param;

/**
 * @author MJ
 */
public interface PageMapper<Po, P_PVo> extends SqlMapper<Po> {
    MpPage<P_PVo> selectPagePVos(MpPage<P_PVo> page,
                                 @Param(Constants.WRAPPER) Wrapper<P_PVo> wrapper);
}
