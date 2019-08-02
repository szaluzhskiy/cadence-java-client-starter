package com.uber.cadence.client.starter.processors;

import com.uber.cadence.client.starter.WorkflowWorkerRegistry;
import com.uber.cadence.client.starter.annotations.Workflow;
import com.uber.cadence.workflow.WorkflowMethod;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class WorkflowAnnotationBeanPostProcessor
    implements BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {

  private BeanFactory beanFactory;

  private WorkflowWorkerRegistry workflowWorkerRegistry = new WorkflowWorkerRegistry();

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

      // create and reg proxy

      ProxyFactory proxyFactory = new ProxyFactory(bean);

      // create method proxy interceptor if it is planned to be used as a regular bean invocation
      // proxyFactory.addAdvice(...); - see MethodInterceptor

      workflowWorkerRegistry.put(workflow.value(), proxyFactory.getProxy());
    }
    return bean;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void afterSingletonsInstantiated() {
    workflowWorkerRegistry.setBeanFactory(beanFactory);
    workflowWorkerRegistry.afterPropertiesSet();
  }

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }
}
