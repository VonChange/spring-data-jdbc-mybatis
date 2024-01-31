package com.vonchange.jdbc.mybatis.core.query;

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