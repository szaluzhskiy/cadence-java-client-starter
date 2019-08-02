package com.uber.cadence.client.starter.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the class is a cadenceClient enabled constructor.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface EnableCadenceClient {
}
