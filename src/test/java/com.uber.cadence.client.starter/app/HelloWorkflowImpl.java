package com.uber.cadence.client.starter.app;

import com.uber.cadence.client.starter.annotations.Workflow;
import com.uber.cadence.workflow.WorkflowMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Workflow("one")
public class HelloWorkflowImpl implements HelloWorkflow {

  @Autowired
  private SimpleService simpleService;

  @Override
  public String process() {
    return "Hello from " + this.getClass() + " including " + simpleService.getClass();
  }
}
