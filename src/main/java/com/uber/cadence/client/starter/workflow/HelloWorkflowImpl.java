package com.uber.cadence.client.starter.workflow;

import com.uber.cadence.client.starter.aspects.HelloWorkflowOne;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HelloWorkflowImpl implements HelloWorkflowOne, TargetSource {

  @Autowired
  private SimpleService simpleService;

  @Override
  public void process() {
    System.out.println("!!! Hello from " + this.getClass() + " including " + simpleService.getClass());
    System.out.println(simpleService.foo("called from HelloWorkflowImpl"));
  }

  @Override
  public Class<?> getTargetClass() {
    return HelloWorkflowOne.class;
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public Object getTarget() throws Exception {
    return this;
  }

  @Override
  public void releaseTarget(Object o) throws Exception {
    System.out.println("releaseTarget " + o);
  }
}
