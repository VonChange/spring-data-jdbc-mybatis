package org.springframework.data.jdbc.repository.query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ConfigLocation {

	/**
	 * The mapping table name.
	 */
	String value() default "";
}