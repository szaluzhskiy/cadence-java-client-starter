package com.uber.cadence.client.starter;

import com.uber.cadence.client.starter.config.CadenceProperties;
import com.uber.cadence.client.starter.config.CadenceProperties.WorkflowOption;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WorkerStarter {

  private final CadenceProperties cadenceProperties;

  @EventListener
  public void start(ContextRefreshedEvent event) {

    Worker.Factory factory = new Worker.Factory(cadenceProperties.getHost(),
        cadenceProperties.getPort(), cadenceProperties.getDomain());

//    Worker contractWorker = factory.newWorker(cadenceProperties.contractCreateOptions().getTasklist(),
//        getWorkerOptions(cadenceProperties.contractCreateOptions()));
//    contractWorker.registerWorkflowImplementationTypes(InsuranceContractCreateWorkflowImpl.class);
//
//    Worker paymentWorker = factory.newWorker(cadenceProperties.paymentCreateOptions().getTasklist(),
//        getWorkerOptions(cadenceProperties.paymentCreateOptions()));
//    paymentWorker.registerWorkflowImplementationTypes(InsuranceContractPaymentWorkflowImpl.class);

    factory.start();

  }

  private WorkerOptions getWorkerOptions(WorkflowOption option) {
    return new WorkerOptions.Builder()
        .setMaxConcurrentActivityExecutionSize(option.getActivityPoolSize())
        .setMaxConcurrentWorkflowExecutionSize(option.getWorkflowPoolSize())
        .build();
  }

}
