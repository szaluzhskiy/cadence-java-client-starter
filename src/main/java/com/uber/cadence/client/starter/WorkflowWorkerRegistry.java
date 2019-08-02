package com.uber.cadence.client.starter;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

public class WorkflowWorkerRegistry implements BeanFactoryAware, InitializingBean {

  private Map<String, Object> workflowProxies = new HashMap<>();

  private BeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void afterPropertiesSet() {
    //TODO register workflow proxies as impls
  }

  public Object put(String workflowName, Object proxy) {
    return workflowProxies.put(workflowName, proxy);
  }
}
