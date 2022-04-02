package io.github.codermjlee.pojo.field;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.codermjlee.common.util.Classes;
import io.github.codermjlee.common.util.Streams;
import io.github.codermjlee.common.util.Strings;
import io.github.codermjlee.pojo.field.anno.Cascade;
import io.github.codermjlee.pojo.field.anno.Field;
import io.github.codermjlee.pojo.field.info.FieldInfo;
import io.github.codermjlee.pojo.field.info.TableInfo;
import io.github.codermjlee.web.msg.Msgs;
import io.github.codermjlee.web.util.Springs;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@SuppressWarnings("unchecked")
public class FieldAspect {
    @Autowired
    private ResourceLoader resourceLoader;

    private final ThreadLocal<Boolean> disable = new ThreadLocal<>();

    public void setDisable(boolean disable) {
        this.disable.set(disable);
    }

    public void removeDisable() {
        this.disable.remove();
    }

    public Object handleRemove(ProceedingJoinPoint point) throws Throwable {
        Boolean bool = disable.get();
        if (bool != null && bool) return point.proceed();
        Object target = point.getTarget();
        if (!(target instanceof BaseMapper)) return point.proceed();

        // 获取模型类型
        Class<?> poCls = Classes.getInterfaceGenericType(target.getClass().getInterfaces()[0]);

        // 表格
        TableInfo table = TableInfo.get(poCls);
        if (table == null) return point.proceed();

        // 主键
        FieldInfo mainField = table.getMainField();
        if (mainField == null) return point.proceed();

        // 获取外键约束
        List<FieldInfo> subFields = mainField.getSubFields();
        if (CollectionUtils.isEmpty(subFields)) return point.proceed();

        // 获取参数值
        Object arg = point.getArgs()[0];
        List<Object> ids;
        if (arg instanceof List) {
            ids = (List<Object>) arg;
        } else {
            ids = new ArrayList<>();
            ids.add(arg);
        }

        // 监控哪些不可以删除的
        String[] values = mainField.getUnremovableValues();
        if (values != null && values.length > 0) {
            for (Object id : ids) {
                String idStr = id.toString();
                for (String value : values) {
                    if (!idStr.equals(value)) continue;
                    Msgs.raise(String.format("【%s=%s】不可以删除", mainField.getColumn(), value));
                }
            }
        }

        for (FieldInfo subField : subFields) {
            TableInfo subTable = subField.getTable();
            IService<Class<?>> service = getService(subTable.getCls());
            if (service == null) continue;
            // 需要删除的子表数据的主键
            List<Object> subIds = null;
            long subCount;
            QueryWrapper<Class<?>> wrapper;
            try {
                wrapper = new QueryWrapper<>();
                wrapper.select("id");
                wrapper.in(subField.getColumn(), ids);
                subIds = service.listObjs(wrapper);
                subCount = subIds.size();
            } catch (Exception e) { // 没有id
                wrapper = new QueryWrapper<>();
                wrapper.in(subField.getColumn(), ids);
                subCount = service.count(wrapper);
            }
            if (subCount == 0) continue;

            if (subField.getCascade() == Cascade.DEFAULT) { // 默认
                Msgs.raise(String.format("关联着【%d】条【%s】数据，无法直接删除！",
                        subCount, subTable.getTable()));
            } else { // 删除关联数据
                if (subIds != null) {
                    service.removeByIds(Streams.map(subIds, o -> (Serializable) o));
                } else {
                    service.remove(wrapper);
                }
            }
        }
        return point.proceed();
    }

    public Object handleSaveOrUpdate(ProceedingJoinPoint point) throws Throwable {
        Boolean bool = disable.get();
        if (bool != null && bool) return point.proceed();
        Object target = point.getTarget();
        if (!(target instanceof BaseMapper)) return point.proceed();

        // 参数
        Object model = point.getArgs()[0];
        Class<?> poCls = model.getClass();
        // 表格
        TableInfo table = TableInfo.get(poCls);
        if (table == null) return point.proceed();

        // 获取外键约束
        Collection<FieldInfo> subFields = table.getSubFields().values();
        if (CollectionUtils.isEmpty(subFields)) return point.proceed();

        // 遍历外键约束
        for (FieldInfo subField : subFields) {
            List<FieldInfo> mainFields = subField.getMainFields();
            if (CollectionUtils.isEmpty(mainFields)) continue;
            // 引用的主键超过1个，无法智能处理，需要手动处理
            if (mainFields.size() > 1) continue;

            Object subValue = subField.getField().get(model);
            // 跳过空值（代表此字段不进行更新）
            if (subValue == null) continue;

            // 查看白名单值（可以不检查数据库中的外键值是否合理）
            String[] whiteSubValues = subField.getWhiteValues();
            if (whiteSubValues != null && whiteSubValues.length > 0) {
                String subValueStr = subValue.toString();
                boolean skip = false;
                for (String whiteSubValue : whiteSubValues) {
                    if (!subValueStr.equals(whiteSubValue)) continue;
                    skip = true;
                    break;
                }
                if (skip) continue;
            }

            // 唯一的一个主键
            FieldInfo mainField = mainFields.get(0);
            IService<Class<?>> service = getService(mainField.getTable().getCls());
            QueryWrapper<Class<?>> wrapper = new QueryWrapper<>();
            wrapper.eq(mainField.getColumn(), subValue);
            if (service.count(wrapper) == 0) {
                Msgs.raise(String.format("【%s=%s】不存在", subField.getColumn(), subValue));
            }
        }
        return point.proceed();
    }

    public void scan(String path) throws Exception {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        Resource[] rs = resolver.getResources(path);
        if (rs.length == 0) {
            Msgs.raise("SCAN_PATH配置错误，找不到任何类信息");
        }

        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourceLoader);
        for (Resource r : rs) { // 去重
            parseCls(readerFactory.getMetadataReader(r).getClassMetadata().getClassName());
        }
    }

    private IService<Class<?>> getService(Class<?> poCls) {
        try {
            return Springs.get(Strings.firstLetterLowercase(poCls.getSimpleName()) + "ServiceImpl");
        } catch (Exception e) {
            return null;
        }
    }

    private void parseCls(String clsName) throws Exception {
        // 跳过内部类
        if (clsName.contains("$")) return;
        Class<?> subCls = Class.forName(clsName);
        TableInfo subTable = TableInfo.get(subCls, true);
        Classes.enumerateFields(subCls, (subField, curCls) -> {
            Field ff = subField.getAnnotation(Field.class);
            parseForeignField(subTable, subField, ff);

            Field.ForeignFields ffs = subField.getAnnotation(Field.ForeignFields.class);
            if (ffs == null) return null;
            for (Field subFf : ffs.value()) {
                parseForeignField(subTable, subField, subFf);
            }
            return null;
        });
    }

    private void parseForeignField(TableInfo subTable,
                                   java.lang.reflect.Field subField,
                                   Field ff) throws Exception {
        // 跳过没有ForeignField注解的属性
        if (ff == null) return;
        // 【外键】引用的主表类
        Class<?> mainCls = Classes.notObject(ff.mainTable(), ff.value());
        // 说明ForeignField注解的是主键属性（因为缺乏mainCls）
        if (mainCls == null || mainCls.equals(Object.class)) return;

        // 主表中的主键属性
        java.lang.reflect.Field mainField = Classes.getField(mainCls, ff.mainField());
        // 跳过错误（找不到）的主键属性
        if (mainField == null) return;

        // 存储到缓存中
        TableInfo mainTable = TableInfo.get(mainCls, true);
        FieldInfo subFieldInfo = subTable.getSubField(subField);
        FieldInfo mainFieldInfo = mainTable.getMainField(mainField);

        // 对象之间的关系
        subFieldInfo.addMainField(mainFieldInfo);
        mainFieldInfo.addSubField(subFieldInfo);
    }
}
