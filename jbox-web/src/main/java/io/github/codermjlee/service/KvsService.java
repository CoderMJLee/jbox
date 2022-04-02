package io.github.codermjlee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.codermjlee.common.util.Strings;
import io.github.codermjlee.pojo.po.Kvs;

public interface KvsService extends IService<Kvs> {
    boolean remove(String key);
    boolean setValue(String key, String value);
    String getValue(String key);
    boolean setJsonValue(String key, Object obj);
    <T> T getJsonValue(String key, Class<T> cls);
    <T> T getJsonValue(Class<T> cls);

    static String defaultKey(Class cls) {
        if (cls == null) return null;
        return Strings.camel2underline(cls.getSimpleName());
    }
}

