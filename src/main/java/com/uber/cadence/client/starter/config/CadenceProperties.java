package com.uber.cadence.client.starter.config;

import com.uber.cadence.activity.ActivityOptions;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.cadence")
public class CadenceProperties {

  private String host;

  private String domain;

  private Integer port;

  private Boolean alwaysSave;

  private Map<String, WorkflowOption> workflows;

  private Map<String, ActivityOptions.Builder> activities;

  @Data
  @NoArgsConstructor
  public static class WorkflowOption {

    private String taskList;

    private Integer executionTimeout;

    private Integer activityPoolSize;

    private Integer workflowPoolSize;

  }

}
