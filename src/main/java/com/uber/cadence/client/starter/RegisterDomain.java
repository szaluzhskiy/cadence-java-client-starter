package com.uber.cadence.client.starter;

import com.uber.cadence.DomainAlreadyExistsError;
import com.uber.cadence.RegisterDomainRequest;
import com.uber.cadence.client.starter.config.CadenceBootstrapConfiguration;
import com.uber.cadence.client.starter.config.CadenceProperties;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@ConditionalOnBean(CadenceBootstrapConfiguration.class)
@Configuration
@RequiredArgsConstructor
public class RegisterDomain  {

  private final CadenceProperties cadenceProperties;

  @EventListener
  public void register(ContextRefreshedEvent event) throws Exception {
    log.debug("trying to register domain :{} using host:{} and port:{}", cadenceProperties.getDomain(),
        cadenceProperties.getHost(), cadenceProperties.getPort());

    IWorkflowService cadenceService = new WorkflowServiceTChannel(
        cadenceProperties.getHost(), cadenceProperties.getPort());
    RegisterDomainRequest request = new RegisterDomainRequest();
    request.setDescription("sample domain");
    request.setEmitMetric(false);
    request.setName(cadenceProperties.getDomain());
    int retentionPeriodInDays = 5;
    request.setWorkflowExecutionRetentionPeriodInDays(retentionPeriodInDays);
    try {
      cadenceService.RegisterDomain(request);
      log.debug("Successfully registered domain {} with retentionDays={}", cadenceProperties.getDomain(),
          retentionPeriodInDays);
    } catch (DomainAlreadyExistsError e) {
      log.error("domain  already exists {} {}", cadenceProperties.getDomain(), e);
    }

  }
}
