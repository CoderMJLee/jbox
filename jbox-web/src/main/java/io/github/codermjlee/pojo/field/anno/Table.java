package io.github.codermjlee.pojo.field.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * 表名
     */
    String value() default "";

    /**
     * 表名
     */
    String name() default "";
}
