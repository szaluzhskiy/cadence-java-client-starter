package com.uber.cadence.client.starter.processors;

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import com.uber.cadence.client.WorkflowOptions.Builder;
import com.uber.cadence.client.starter.annotations.Activity;
import com.uber.cadence.client.starter.annotations.Workflow;
import com.uber.cadence.client.starter.config.CadenceProperties;
import com.uber.cadence.client.starter.config.CadenceProperties.WorkflowOption;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;
import com.uber.cadence.workflow.Functions.Func;
import com.uber.cadence.workflow.WorkflowMethod;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
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
  private final Set<String> classes = new HashSet<>();
  private Supplier<?> sup;

  private BeanFactory beanFactory;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    // check if class was already processed
    if (classes.contains(bean.getClass().getName())) {
      return bean;
    }

    // check annotations present
    Class<?> targetClass = AopUtils.getTargetClass(bean);

    Workflow workflow = AnnotationUtils.findAnnotation(targetClass, Workflow.class);
    if (workflow != null) {
      return processWorkflow(targetClass, workflow, bean, beanName);
    }

    return bean;
  }

  private Object processWorkflow(Class<?> targetClass, Workflow workflow, Object bean, String beanName) {
    Set<Method> methods = MethodIntrospector.selectMethods(targetClass,
        (ReflectionUtils.MethodFilter) method ->
            AnnotationUtils.findAnnotation(method, WorkflowMethod.class) != null);

    if (methods.isEmpty()) {

      log.info("No @WorkflowMethod found on bean {}", bean.getClass());
      return bean;

    } else {

      // create proxy for method interception
      log.info("Registering worker for {}", targetClass);

      WorkflowOption option = cadenceProperties.getWorkflows().get(workflow.value());
      Worker worker = workerFactory.newWorker(option.getTaskList(), getWorkerOptions(option));

      Class<?> interfac = getWorkflowMethodInterface(targetClass);

      ProxyFactory proxyFactory = new ProxyFactory();
      proxyFactory.setTargetClass(bean.getClass());
      proxyFactory.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation -> {
        Method method = invocation.getMethod();
        if (methods.contains(method)) {
          WorkflowOptions options = new Builder()
              .setTaskList(workflow.value())
              .setExecutionStartToCloseTimeout(
                  Duration.ofSeconds(option.getExecutionTimeout()))
              .build();

          Object stub = workflowClient.newWorkflowStub(interfac, options);

          return stub.getClass().getMethod(method.getName()).invoke(stub, invocation.getArguments());
        } else {
          return invocation.proceed();
        }
      });

      //find and register activities
      val activitiesImpls = Arrays.stream(bean.getClass().getDeclaredFields())
          .filter(field -> field.isAnnotationPresent(Activity.class))
          .map(field -> {
            try {
              field.setAccessible(true);
              return field.get(bean);
            } catch (Exception ex) {
              return null;
            }
          })
          .filter(Objects::nonNull)
          .toArray();

      worker.registerActivitiesImplementations(activitiesImpls);

      // register implementation factory with prototype scoped beans by target workflow interface,
      // and set activity stubs for fields;
      worker.addWorkflowImplementationFactory((Class<Object>) interfac,
          createWorkflowFactory(bean, beanName));

      // to keep track of already processed classes;
      classes.add(bean.getClass().getName());

      return proxyFactory.getProxy();
    }
  }

  private Func<Object> createWorkflowFactory(Object bean, String beanName) {
    return () -> {

      Object prototype = ((DefaultListableBeanFactory) beanFactory).configureBean(bean, beanName);

      Arrays.stream(bean.getClass().getDeclaredFields())
          .filter(field -> field.isAnnotationPresent(Activity.class))
          .forEach(field -> {
            try {
              FieldUtils.removeFinalModifier(field);
              FieldUtils.writeField(field, prototype,
                  createActivityStub(field.getType(), field.getAnnotation(Activity.class)), true);
            } catch (Exception ex) {
                log.info("Failed to create activity stub for '{}'", field.getType());
            }
          });

      return prototype;
    };
  }

  private <T> T createActivityStub(Class<T> type, Activity activity) {
    ActivityOptions activityOptions = cadenceProperties.getActivities().get(activity.value()).build();
    return com.uber.cadence.workflow.Workflow.newActivityStub(type, activityOptions);
  }

  private Class<?> getWorkflowMethodInterface(Class<?> targetClass) {
    List<Class<?>> workflowInterfaces = Arrays.stream(targetClass.getInterfaces())
        .filter(cl -> Arrays.stream(cl.getDeclaredMethods())
            .anyMatch(method -> method.isAnnotationPresent(WorkflowMethod.class)))
        .collect(Collectors.toList());

    if (workflowInterfaces.size() != 1) {
      throw new IllegalArgumentException(String.format(
          "Target class '%s' must implement only one interface with @WorkflowMethod annotated method",
          targetClass.getName()));
    } else {
      return workflowInterfaces.get(0);
    }
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
