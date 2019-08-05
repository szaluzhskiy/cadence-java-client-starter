package com.uber.cadence.client.starter.app;

import com.uber.cadence.client.starter.annotations.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Workflow("two")
@Configurable(autowire = Autowire.BY_TYPE, preConstruction = true)
public class HelloWorkflowImplTwo implements HelloWorkflow {

  @Autowired
  private SimpleService simpleService;

  @Override
  public void process() {
    System.out.println(
        "Hello from " + this.getClass() + " including " + simpleService.getClass()
    );
  }

}
