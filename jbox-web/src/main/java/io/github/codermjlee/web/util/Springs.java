package io.github.codermjlee.web.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MJ
 */
@Component
@SuppressWarnings("unchecked")
public class Springs implements ApplicationContextAware {
    private static ApplicationContext ctx;
    private static final Map<Class<?>, Object> BEANS = new HashMap<>();

    public static void setCtx(ApplicationContext ctx) {
        Springs.ctx = ctx;
    }

    public static void put(Class<?> cls, Object obj) {
        BEANS.put(cls, obj);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T get(String name) {
        try {
            return (T) ctx.getBean(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T get(Class<T> cls) {
        try {
            Object bean = BEANS.get(cls);
            return bean != null ? (T) bean : ctx.getBean(cls);
        } catch (Exception e) {
            return null;
        }
    }
}
