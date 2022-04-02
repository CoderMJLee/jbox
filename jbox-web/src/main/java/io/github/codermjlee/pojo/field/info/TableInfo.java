package io.github.codermjlee.pojo.field.info;

import io.github.codermjlee.common.util.Strings;
import io.github.codermjlee.pojo.field.anno.Cascade;
import io.github.codermjlee.pojo.field.anno.Field;
import io.github.codermjlee.pojo.field.anno.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TableInfo {
    private static final Map<Class<?>, TableInfo> cache = new HashMap<>();

    private Class<?> cls;
    private String table;
    private FieldInfo mainField;
    /**
     * key是field的属性名
     */
    private Map<String, FieldInfo> subFields = new HashMap<>();

    public static TableInfo get(Class<?> tableCls) {
        return get(tableCls, false);
    }

    /**
     * 从缓存中取出table
     * @param newIfAbsent 如果找不到就新建一个
     */
    public static TableInfo get(Class<?> tableCls, boolean newIfAbsent) {
        if (!newIfAbsent) return cache.get(tableCls);
        return cache.computeIfAbsent(tableCls, k -> {
            TableInfo table = new TableInfo();
            // 类
            table.setCls(tableCls);

            // 表名
            Table tableAnno = tableCls.getAnnotation(Table.class);
            String tableName;
            if (tableAnno != null) {
                tableName = Strings.notEmpty(tableAnno.name(), tableAnno.value());
            } else {
                tableName = Strings.camel2underline(tableCls.getSimpleName());
            }
            table.setTable(tableName);
            return table;
        });
    }

    public FieldInfo getMainField(java.lang.reflect.Field field) {
        if (mainField == null) {
            mainField = new FieldInfo();
            mainField.setTable(this);
            mainField.setField(field);
            mainField.setColumn(getFieldColumn(field));
            Field ff = field.getAnnotation(Field.class);
            if (ff != null) {
                mainField.setCascade(ff.cascade());
                mainField.setUnremovableValues(ff.unremovableValues());
            } else {
                mainField.setCascade(Cascade.DEFAULT);
            }
        }
        return mainField;
    }

    public FieldInfo getSubField(java.lang.reflect.Field field) {
        String fieldName = field.getName();
        return subFields.computeIfAbsent(fieldName, k -> {
            FieldInfo subField = new FieldInfo();
            subField.setTable(this);
            subField.setField(field);
            subField.setColumn(getFieldColumn(field));
            Field ff = field.getAnnotation(Field.class);
            if (ff != null) {
                subField.setWhiteValues(ff.whiteValues());
                subField.setCascade(ff.cascade());
            } else {
                subField.setCascade(Cascade.DEFAULT);
            }
            return subField;
        });
    }

    private String getFieldColumn(java.lang.reflect.Field field) {
        Field ff = field.getAnnotation(Field.class);
        if (ff != null) {
            String column = ff.column();
            if (!Strings.isEmpty(column)) return column;
        }
        return Strings.camel2underline(field.getName());
    }
}
