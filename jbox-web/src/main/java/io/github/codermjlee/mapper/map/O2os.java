package io.github.codermjlee.mapper.map;


import io.github.codermjlee.common.util.Streams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author MJ
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class O2os {
    private static final Map<Class, Function> funcs = new HashMap<>();
    private static final Map<Class, Map<Class, Function>> otherFuncs = new HashMap<>();

    public static <T, R> void map(Class<T> cls, Function<T, R> func) {
        funcs.put(cls, func);
    }

    public static <T, R> void map(Class<T> cls, Class<R> cls2, Function<T, R> func) {
        otherFuncs.computeIfAbsent(cls, k -> new HashMap<>()).put(cls2, func);
    }

    public static <T, R> List<R> os2os(List<T> os) {
        if (os == null || os.size() == 0) return null;
        Function func = funcs.get(os.get(0).getClass());
        return (List<R>) (func == null ? os : Streams.map(os, o -> (R) func.apply(o)));
    }

    public static <T, R> R o2o(T o) {
        if (o == null) return null;
        Function func = funcs.get(o.getClass());
        return (R) (func == null ? o : func.apply(o));
    }

    public static <T, R> R o2o(T o, Class<R> cls) {
        if (o == null || cls == null) return null;
        Map<Class, Function> map = otherFuncs.get(o.getClass());
        if (map == null) return null;
        Function func = map.get(cls);
        return (R) (func == null ? o : func.apply(o));
    }
}
