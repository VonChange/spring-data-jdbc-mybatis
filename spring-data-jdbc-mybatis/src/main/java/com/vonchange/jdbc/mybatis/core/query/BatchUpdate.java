package com.vonchange.jdbc.mybatis.core.query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
public @interface BatchUpdate {
    /**
     * Batch Size
     */
    int size() default 5000;
}