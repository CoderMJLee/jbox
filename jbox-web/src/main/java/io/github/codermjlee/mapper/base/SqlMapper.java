package io.github.codermjlee.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author MJ
 */
public interface SqlMapper<Po> extends BaseMapper<Po> {
    @Update("${sql}")
    void execute(@Param(value = "sql") String sql);
}
