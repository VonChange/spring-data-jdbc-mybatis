package com.vonchange.spring.data.mybatis.mini.jdbc.repository.query;

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