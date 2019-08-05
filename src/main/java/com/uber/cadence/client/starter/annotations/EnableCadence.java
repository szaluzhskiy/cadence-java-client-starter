package com.uber.cadence.client.starter.annotations;


import com.uber.cadence.client.starter.config.CadenceBootstrapConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.SpringConfiguredConfiguration;

/**
 * Indicates that cadence auto-configuration should be applied
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({CadenceBootstrapConfiguration.class, SpringConfiguredConfiguration.class})
public @interface EnableCadence {
}
