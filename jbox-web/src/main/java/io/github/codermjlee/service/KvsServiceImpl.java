package io.github.codermjlee.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.codermjlee.common.util.Jsons;
import io.github.codermjlee.mapper.KvsMapper;
import io.github.codermjlee.mapper.mp.MpLambdaQueryWrapper;
import io.github.codermjlee.pojo.po.Kvs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class KvsServiceImpl
    extends ServiceImpl<KvsMapper, Kvs>
    implements KvsService {
    private final Map<String, String> VALUE_MAP = new HashMap<>();
    private final Map<String, Object> JSON_VALUE_MAP = new HashMap<>();

    public boolean setValue(String key, String value) {
        if (key == null || value == null) return false;
        VALUE_MAP.put(key, value);
        MpLambdaQueryWrapper<Kvs> wrapper = new MpLambdaQueryWrapper<>();
        wrapper.eqs(key, Kvs::getK);
        Kvs kvs = baseMapper.selectOne(wrapper);
        if (kvs == null) {
            kvs = new Kvs();
            kvs.setK(key);
        }
        kvs.setV(value);
        return saveOrUpdate(kvs);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public String getValue(String key) {
        if (key == null) return null;
        String value = VALUE_MAP.get(key);
        if (value == null) {
            MpLambdaQueryWrapper<Kvs> wrapper = new MpLambdaQueryWrapper<>();
            wrapper.eqs(key, Kvs::getK);
            Kvs kvs = baseMapper.selectOne(wrapper);
            value = kvs != null ? kvs.getV() : null;
            VALUE_MAP.put(key, value);
        }
        return value;
    }

    public boolean setJsonValue(String key, Object obj) {
        if (key == null || obj == null) return false;
        JSON_VALUE_MAP.put(key, obj);
        return setValue(key, Jsons.getString(obj));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public <T> T getJsonValue(String key, Class<T> cls) {
        if (key == null || cls == null) return null;
        Object jsonValue = JSON_VALUE_MAP.get(key);
        if (jsonValue == null) {
            String value = getValue(key);
            jsonValue = value == null ? null : Jsons.getObj(value, cls);
            JSON_VALUE_MAP.put(key, jsonValue);
        }
        return (T) jsonValue;
    }

    @Override
    public <T> T getJsonValue(Class<T> cls) {
        if (cls == null) return null;
        return getJsonValue(KvsService.defaultKey(cls), cls);
    }

    @Override
    public boolean remove(String key) {
        if (key == null) return false;
        MpLambdaQueryWrapper<Kvs> wrapper = new MpLambdaQueryWrapper<>();
        wrapper.eqs(key, Kvs::getK);
        boolean ret = baseMapper.delete(wrapper) > 0;
        if (ret) {
            VALUE_MAP.remove(key);
            JSON_VALUE_MAP.remove(key);
        }
        return ret;
    }
}
