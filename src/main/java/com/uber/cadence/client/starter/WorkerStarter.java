package com.uber.cadence.client.starter;

import com.uber.cadence.client.starter.config.CadenceProperties;
import com.uber.cadence.client.starter.config.CadenceProperties.WorkflowOption;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WorkerStarter {

  private final CadenceProperties cadenceProperties;
  private final ApplicationContext ctx;

  @EventListener
  public void start(ContextRefreshedEvent event) {

    Worker.Factory factory = new Worker.Factory(cadenceProperties.getHost(),
        cadenceProperties.getPort(), cadenceProperties.getDomain());

    for (Map.Entry<String, WorkflowOption> workFlow: cadenceProperties.getWorkflows().entrySet()) {
      WorkflowOption workflowOption = cadenceProperties.getWorkflows().get(workFlow.getKey());
      Worker worker = factory
          .newWorker(workflowOption.getTaskList(), getWorkerOptions(workflowOption));

      String beanName = workFlow.getKey();

      BeanDefinitionCustomizer bdc = new BeanDefinitionCustomizer() {
        @Override
        public void customize(BeanDefinition beanDefinition) {
          beanDefinition.setFactoryBeanName(beanName);
        }
      };

      ((GenericApplicationContext) ctx).registerBean(Worker.class, () -> worker, bdc) ;

    }

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
