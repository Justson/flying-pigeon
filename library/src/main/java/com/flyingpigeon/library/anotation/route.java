package com.flyingpigeon.library.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author xiaozhongcen
 * @date 20-6-12
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target({PARAMETER,METHOD})
public @interface route {

    String value();

    boolean encoded() default false;
}
