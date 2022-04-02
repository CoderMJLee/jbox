package io.github.codermjlee.pojo.field.info;

import io.github.codermjlee.pojo.field.anno.Cascade;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FieldInfo {
    /**
     * 属性名
     */
    private Field field;
    /**
     * 列名
     */
    private String column;
    /**
     * 表
     */
    private TableInfo table;
    /**
     * 外键的一些例外值
     */
    private String[] whiteValues;
    /**
     * 主键的一些不可删除值
     */
    private String[] unremovableValues;
    /**
     * 自己引用着的一些属性
     */
    private List<FieldInfo> mainFields;
    /**
     * 引用着自己的一些属性
     */
    private List<FieldInfo> subFields;
    /**
     * 级联类型
     */
    private Cascade cascade;

    public void setField(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    public void addSubField(FieldInfo subField) {
        if (subFields == null) {
            subFields = new ArrayList<>();
        } else if (subFields.contains(subField)) {
            return;
        }
        subFields.add(subField);
    }

    public void addMainField(FieldInfo mainField) {
        if (mainFields == null) {
            mainFields = new ArrayList<>();
        } else if (mainFields.contains(mainField)) {
            return;
        }
        mainFields.add(mainField);
    }
}
