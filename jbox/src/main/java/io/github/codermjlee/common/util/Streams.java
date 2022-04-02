package io.github.codermjlee.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 集合处理
 *
 * @author MJ
 */
public class Streams {
    public static <T, R> List<R> map(Collection<T> list, Function<T, R> function) {
        if (list == null || list.size() == 0 || function == null) return null;
        List<R> retList = new ArrayList<>();
        for (T t : list) {
            retList.add(function.apply(t));
        }
        return retList;
    }
}
