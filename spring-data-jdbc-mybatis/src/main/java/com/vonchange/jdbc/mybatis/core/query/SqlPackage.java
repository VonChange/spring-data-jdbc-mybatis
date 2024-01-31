package com.vonchange.jdbc.mybatis.core.query;

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