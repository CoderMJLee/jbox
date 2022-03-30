package com.mj.common.util;

import com.mj.common.Stop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类相关的操作
 *
 * @author MJ
 */
@SuppressWarnings("rawtypes")
public class Classes {
    /**
     * 获得泛型类型
     */
    public static Class[] getGenericTypes(Class cls) {
        if (cls == null) return null;
        Type superCls = cls.getGenericSuperclass();
        // 没有泛型参数就直接返回
        if (!(superCls instanceof ParameterizedType)) return null;

        // 获取泛型参数
        Type[] args = ((ParameterizedType) superCls).getActualTypeArguments();
        if (args == null || args.length == 0) return null;

        Class[] clses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Type type = args[i];
            if (type instanceof Class) {
                clses[i] = (Class) type;
            } else if (type instanceof ParameterizedType) {
                clses[i] = (Class) ((ParameterizedType) type).getRawType();
            }
        }
        // 返回泛型参数
        return clses;
    }

    /**
     * 获得第一个泛型类型
     */
    public static Class<?> getGenericType(Class<?> cls) {
        Class[] clses = getGenericTypes(cls);
        return clses == null ? null : clses[0];
    }

    /**
     * 返回第一个不是Object.class的类
     */
    public static Class<?> notObject(Class<?>... sources) {
        if (sources == null) return null;
        for (Class<?> source : sources) {
            if (!source.equals(Object.class)) return source;
        }
        return null;
    }

    /**
     * 获取cls类中的fieldName属性
     */
    public static Field getField(Class<?> cls, String fieldName) throws Exception {
        return enumerateFields(cls, (field, curCls) -> {
            if (field.getName().equals(fieldName)) return Stop.create(field);
            return null;
        });
    }

    public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) throws Exception {
        if (name == null || cls == null) return null;
        Class<?> curCls = cls;
        while (curCls != null && !curCls.equals(Object.class)) {
            try {
                Method method = curCls.getDeclaredMethod(name, parameterTypes);
                if (method != null) return method;
            } catch (Exception e) {
                //
            }
            curCls = curCls.getSuperclass();
        }
        return null;
    }

    public static Method getMethod(Class<?> cls, String methodName) throws Exception {
        return enumerateMethods(cls, (method, curCls) -> {
            if (method.getName().equals(methodName)) return Stop.create(method);
            return null;
        });
    }

    /**
     * 遍历cls的所有方法
     */
    public static <T> T enumerateMethods(Class<?> cls,
                                         StoppableConsumer<Method, T> stoppableConsumer) throws Exception {
        if (stoppableConsumer == null || cls == null) return null;
        Class<?> curCls = cls;
        while (curCls != null && !curCls.equals(Object.class)) {
            for (Method method : curCls.getDeclaredMethods()) {
                Stop<T> stop = stoppableConsumer.accept(method, curCls);
                if (stop != null) return stop.getData();
            }
            curCls = curCls.getSuperclass();
        }
        return null;
    }

    /**
     * 遍历cls的所有属性
     */
    public static <T> T enumerateFields(Class<?> cls,
                                        StoppableConsumer<Field, T> stoppableConsumer) throws Exception {
        if (stoppableConsumer == null || cls == null) return null;
        Class<?> curCls = cls;
        while (curCls != null && !curCls.equals(Object.class)) {
            for (Field field : curCls.getDeclaredFields()) {
                Stop<T> stop = stoppableConsumer.accept(field, curCls);
                if (stop != null) return stop.getData();
            }
            curCls = curCls.getSuperclass();
        }
        return null;
    }

    public interface StoppableConsumer<P, T> {
        Stop<T> accept(P field, Class<?> ownerCls) throws Exception;
    }

//    public interface FieldConsumer<T> {
//        Stop<T> accept(Field field, Class<?> ownerCls) throws Exception;
//    }
}
