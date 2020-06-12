package com.flyingpigeon.library.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author xiaozhongcen
 * @date 20-6-12
 * @since 1.0.0
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface RequestLarge {
}
