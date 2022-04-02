package io.github.codermjlee.mapper.util;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.codermjlee.common.util.Strings;
import io.github.codermjlee.mapper.map.O2os;
import io.github.codermjlee.mapper.mp.MpLambdaQueryWrapper;
import io.github.codermjlee.mapper.mp.MpPage;
import io.github.codermjlee.mapper.mp.MpQueryWrapper;
import io.github.codermjlee.pojo.vo.req.page.PageQVo;
import io.github.codermjlee.pojo.vo.resp.page.PageListPVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.util.function.Consumer;

public class Mps {
    public static <Q extends PageQVo, P, T> PageListPVo<P>
    page(
        BaseMapper<T> mapper,
        Q qVo,
        Consumer<MpLambdaQueryWrapper<T>> wrapperConsumer
    ) {
        MpLambdaQueryWrapper<T> wrapper = new MpLambdaQueryWrapper<>();
        if (wrapperConsumer != null) {
            wrapperConsumer.accept(wrapper);
        }
        orderBy(wrapper, qVo);
        return mapper
            .selectPage(new MpPage<>(qVo), wrapper)
            .buildPVos(O2os::os2os);
    }

    /**
     * 当PVo的字段名跟JOIN表的字段名不一致时，需要使用此方法进行查询
     */
    public static <Q extends PageQVo, P, M> PageListPVo<P>
    page(
        SelectPage<P> selectPage,
        Q qVo,
        Consumer<MpQueryWrapper<P>> wrapperConsumer
    ) {
        MpQueryWrapper<P> wrapper = new MpQueryWrapper<>();
        if (wrapperConsumer != null) {
            wrapperConsumer.accept(wrapper);
        }
        orderBy(wrapper, qVo);
        return selectPage.select(new MpPage<>(qVo), wrapper).buildPVos();
    }

    private static <T> void orderBy(Join<T> wrapper, PageQVo qVo) {
        if (qVo == null || wrapper == null) return;
        String sortColumn = qVo.getSortColumn();
        if (sortColumn != null) {
            sortColumn = Strings.camel2underline(sortColumn);
            String sql = "ORDER BY " + sortColumn + " ";
            if (qVo.isDesc()) {
                wrapper.last(sql + "DESC");
            } else {
                wrapper.last(sql + "ASC");
            }
        }
    }

    public static <T> String fieldName(SFunction<T, ?> func) {
        return PropertyNamer.methodToProperty(LambdaUtils.extract(func).getImplMethodName());
    }

    public static <T> String column(SFunction<T, ?> func) {
        return Strings.camel2underline(fieldName(func));
    }

    public static <T> String column(String tblPrefix, SFunction<T, ?> func) {
        String column = column(func);
        return tblPrefix == null ? column : tblPrefix + "." + column;
    }

    @FunctionalInterface
    public interface SelectPage<T> {
        MpPage<T> select(MpPage<T> page,
                         @Param(Constants.WRAPPER) Wrapper<T> wrapper);
    }
}
