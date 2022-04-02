package io.github.codermjlee.common.util;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Compares {
    public enum Order {
        ASC, DESC
    }

    @Getter
    @Setter
    public static class Prop<T> {
        private Function<T, Object> func;
        private Order order;
        public Prop(Function<T, Object> func) {
            this(func, Order.ASC);
        }
        public Prop(Function<T, Object> func, Order order) {
            this.func = func;
            this.order = order;
        }
    }

    public static <T> int compare(T o1, T o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        return ((Comparable<T>) o1).compareTo(o2);
    }

    @SafeVarargs
    public static <T> int compareProps(T o1, T o2, Prop<T> ...props) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        if (props == null) return 0;
        for (Prop<T> prop : props) {
            Function<T, Object> func = prop.getFunc();
            if (func == null) return 0;
            int ret = compare(func.apply(o1), func.apply(o2));
            if (prop.getOrder() == Order.DESC) {
                ret *= -1;
            }
            if (ret != 0) return ret;
        }
        return 0;
    }
}
