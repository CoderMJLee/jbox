package io.github.codermjlee.mapper.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.codermjlee.mapper.util.Mps;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class MpQueryWrapper<T> extends QueryWrapper<T> {
    @SafeVarargs
    public final MpQueryWrapper<T> likes(Object val, SFunction<T, ?>... funcs) {
        if (val == null) return this;
        String str = val.toString();
        if (str.length() == 0) return this;
        return (MpQueryWrapper<T>) nested((w) -> {
            for (SFunction<T, ?> func : funcs) {
                w.like(Mps.column(func), str).or();
            }
        });
    }

    public final MpQueryWrapper<T> likes(Object val, List<String> columns) {
        if (val == null) return this;
        String str = val.toString();
        if (str.length() == 0) return this;
        return (MpQueryWrapper<T>) nested((w) -> {
            for (String column : columns) {
                w.like(column, str).or();
            }
        });
    }

    public final MpQueryWrapper<T> eqs(Object val, SFunction<T, ?> func) {
        return eqs(val, Mps.column(func));
    }

    public final MpQueryWrapper<T> eqs(Object val, String column) {
        return (val == null) ? this : (MpQueryWrapper<T>) eq(column, val);
    }

    public final MpQueryWrapper<T> ins(Collection<?> val, SFunction<T, ?> func) {
        return ins(val, Mps.column(func));
    }

    public final MpQueryWrapper<T> ins(Collection<?> val, String column) {
        return CollectionUtils.isEmpty(val) ? this : (MpQueryWrapper<T>) in(column, val);
    }

    @Override
    public MpQueryWrapper<T> or() {
        return (MpQueryWrapper<T>) super.or();
    }

    @Override
    public MpQueryWrapper<T> gt(String column, Object val) {
        return (MpQueryWrapper<T>) super.gt(column, val);
    }

    @Override
    public MpQueryWrapper<T> le(String column, Object val) {
        return (MpQueryWrapper<T>) super.le(column, val);
    }

    public final <P> MpQueryWrapper<T> betweens(P p1, P p2, SFunction<T, ?> func) {
        return betweens(p1, p2, Mps.column(func));
    }

    public final <P> MpQueryWrapper<T> betweens(P p1, P p2, String column) {
        if (p1 == null && p2 == null) return this;
        if (p1 != null && p2 == null) {
            return (MpQueryWrapper<T>) ge(column, p1);
        }
        if (p1 == null && p2 != null) {
            return (MpQueryWrapper<T>) le(column, p2);
        }
        return (MpQueryWrapper<T>) between(column, p1, p2);
    }
}
