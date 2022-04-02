package io.github.codermjlee.pojo.field.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Field.ForeignFields.class)
public @interface Field {
    /**
     * 【外键】被引用的主表类
     */
    Class<?> value() default Object.class;

    /**
     * 【外键】被引用的主表类
     */
    Class<?> mainTable() default Object.class;

    /**
     * 【外键】被引用的属性名
     */
    String mainField() default "id";

    /**
     * 【主键】【外键】当前属性在数据库中的字段名
     */
    String column() default "";

    /**
     * 【外键】例外值（这些值可以不在主键值中）
     */
    String[] whiteValues() default {};

    /**
     * 【主键】不可以删除的值
     */
    String[] unremovableValues() default {};

    /**
     * 【外键】级联类型
     */
    Cascade cascade() default Cascade.DEFAULT;

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ForeignFields {
        /**
         * 被引用的主表类
         */
        Field[] value() default {};
    }
}
