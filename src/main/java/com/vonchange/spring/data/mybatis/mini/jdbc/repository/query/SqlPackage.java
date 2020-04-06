package com.vonchange.spring.data.mybatis.mini.jdbc.repository.query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface SqlPackage {

	/**
	 * sql package location
	 */
	String value() default "";
}