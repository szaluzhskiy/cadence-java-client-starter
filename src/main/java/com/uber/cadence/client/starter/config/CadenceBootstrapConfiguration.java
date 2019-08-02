package com.uber.cadence.client.starter.config;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.starter.RegisterDomain;
import com.uber.cadence.client.starter.annotations.EnableCadence;
import com.uber.cadence.client.starter.processors.WorkflowAnnotationBeanPostProcessor;
import com.uber.cadence.worker.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@Configuration
@ConditionalOnClass({EnableCadence.class})
@EnableConfigurationProperties(CadenceProperties.class)
@Import({RegisterDomain.class, WorkflowAnnotationBeanPostProcessor.class})
@RequiredArgsConstructor
public class CadenceBootstrapConfiguration {

  private final CadenceProperties cadenceProperties;

  @Bean
  public Worker.Factory defaultWorkerFactory(CadenceProperties cadenceProperties) {
    return new Worker.Factory(cadenceProperties.getHost(),
        cadenceProperties.getPort(), cadenceProperties.getDomain());
  }

  @Bean
  public WorkflowClient defaultClient(CadenceProperties cadenceProperties) {
    return WorkflowClient.newInstance(
        cadenceProperties.getHost(), cadenceProperties.getPort(), cadenceProperties.getDomain());
  }


}
