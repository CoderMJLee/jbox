package io.github.codermjlee.service.base;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.codermjlee.mapper.mp.MpLambdaQueryWrapper;

/**
 * @author MJ
 */
public interface BaseService<Po> extends IService<Po> {
    Po getOneV2(MpLambdaQueryWrapper<Po> wrapper);
}
