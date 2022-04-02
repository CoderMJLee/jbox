package io.github.codermjlee.mapper.mp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

public class MpLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {
    @SafeVarargs
    public final MpLambdaQueryWrapper<T> likes(Object val, SFunction<T, ?>... funcs) {
        if (val == null) return this;
        String str = val.toString();
        if (str.length() == 0) return this;
        return (MpLambdaQueryWrapper<T>) nested((w) -> {
            for (SFunction<T, ?> func : funcs) {
                w.like(func, str).or();
            }
        });
    }

    @Override
    public MpLambdaQueryWrapper<T> or() {
        return (MpLambdaQueryWrapper<T>) super.or();
    }

    public final MpLambdaQueryWrapper<T> eqs(Object val, SFunction<T, ?> func) {
        return (val == null) ? this : (MpLambdaQueryWrapper<T>) eq(func, val);
    }

    public final <P> MpLambdaQueryWrapper<T> betweens(P p1, P p2, SFunction<T, ?> func) {
        if (p1 == null && p2 == null) return this;
        if (p1 != null && p2 == null) {
            return (MpLambdaQueryWrapper<T>) ge(func, p1);
        }
        if (p1 == null && p2 != null) {
            return (MpLambdaQueryWrapper<T>) le(func, p2);
        }
        return (MpLambdaQueryWrapper<T>) between(func, p1, p2);
    }
}
