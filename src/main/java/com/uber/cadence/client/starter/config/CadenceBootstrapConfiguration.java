package com.uber.cadence.client.starter.config;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.starter.RegisterDomain;
import com.uber.cadence.client.starter.WorkerStarter;
import com.uber.cadence.client.starter.WorkflowWorkerRegistry;
import com.uber.cadence.client.starter.annotations.EnableCadence;
import com.uber.cadence.client.starter.processors.WorkflowAnnotationBeanPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@Configuration
@ConditionalOnClass({EnableCadence.class})
@EnableConfigurationProperties(CadenceProperties.class)
@Import({RegisterDomain.class, WorkerStarter.class}) // could be removed if not needed to be injected somewhere
@RequiredArgsConstructor
public class CadenceBootstrapConfiguration {

  private final CadenceProperties cadenceProperties;

  @SuppressWarnings("rawtypes")
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public WorkflowAnnotationBeanPostProcessor workflowAnnotationProcessor() {
    return new WorkflowAnnotationBeanPostProcessor();
  }

  @Bean
  public WorkflowWorkerRegistry defaultWorkflowWorkerRegistry() {
    return new  WorkflowWorkerRegistry();
  }

  @Bean
  public WorkflowClient defaultClient(CadenceProperties cadenceProperties) {
    return WorkflowClient.newInstance(
        cadenceProperties.getHost(), cadenceProperties.getPort(), cadenceProperties.getDomain());
  }


}
