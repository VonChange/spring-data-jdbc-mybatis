package com.vonchange.spring.data.mybatis.mini.jdbc.repository.query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DataSourceKey {

	/**
	 * 数据源key
	 */
	String value() default "";
}