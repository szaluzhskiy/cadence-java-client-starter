package com.uber.cadence.client.starter.app;

import com.uber.cadence.client.starter.annotations.Activity;
import com.uber.cadence.client.starter.annotations.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Workflow("two")
public class HelloWorkflowImplTwo implements HelloWorkflow {

  @Autowired
  @Activity("ac")
  private SimpleServiceImpl simpleService;

  @Override
  public String process() {
    return "Hello from " + this.getClass() + " with uuid - " + simpleService.simpleWork();
  }

}
