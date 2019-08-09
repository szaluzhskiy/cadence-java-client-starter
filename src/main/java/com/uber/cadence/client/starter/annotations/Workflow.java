package com.uber.cadence.client.starter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that service is an appropriate cadence workflow implementation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Workflow {
  /**
   * Cadence task list name
   */
  String taskList();
}
