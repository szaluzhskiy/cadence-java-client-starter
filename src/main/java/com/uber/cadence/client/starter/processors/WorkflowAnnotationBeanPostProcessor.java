package com.uber.cadence.client.starter.processors;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.starter.WorkflowFactory;
import com.uber.cadence.client.starter.annotations.Workflow;
import com.uber.cadence.client.starter.config.CadenceProperties;
import com.uber.cadence.client.starter.config.CadenceProperties.WorkflowOption;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;
import com.uber.cadence.workflow.WorkflowMethod;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WorkflowAnnotationBeanPostProcessor
    implements BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {

  private final WorkflowClient workflowClient;
  private final CadenceProperties cadenceProperties;
  private final Worker.Factory workerFactory;

  private BeanFactory beanFactory;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    /*
     * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
     */
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    Workflow workflow = AnnotationUtils.findAnnotation(targetClass, Workflow.class);

    if (workflow == null) {
      return bean;
    }

    Set<Method> methods = MethodIntrospector.selectMethods(targetClass,
        (ReflectionUtils.MethodFilter) method ->
            AnnotationUtils.findAnnotation(method, WorkflowMethod.class) != null);

    if (methods.isEmpty()) {

      log.info("No @WorkflowMethod found on bean {}", bean.getClass());
      return bean;

    } else {

      // Регистрируем воркера с имплементацией

      WorkflowOption workflowOption = cadenceProperties.getWorkflows().get(workflow.value());

      Worker worker = workerFactory
          .newWorker(workflowOption.getTaskList(), getWorkerOptions(workflowOption));

      worker.registerWorkflowImplementationTypes(bean.getClass());

      // Добавляем в контекст фэктори с нужными тайп параметрами

      RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(
          WorkflowFactory.class,
          () -> new WorkflowFactory<>(workflowClient, workflowOption, workflow.value(), targetClass.getInterfaces()[0])
      );

      rootBeanDefinition.setTargetType(
          ResolvableType.forClassWithGenerics(WorkflowFactory.class, targetClass.getInterfaces()[0], targetClass)
      );

      ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(workflow.value(), rootBeanDefinition);

    }
    return bean;
  }

  private WorkerOptions getWorkerOptions(WorkflowOption option) {
    return new WorkerOptions.Builder()
        .setMaxConcurrentActivityExecutionSize(option.getActivityPoolSize())
        .setMaxConcurrentWorkflowExecutionSize(option.getWorkflowPoolSize())
        .build();
  }


  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void afterSingletonsInstantiated() {
    workerFactory.start();
  }

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
