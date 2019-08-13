package com.uber.cadence.client.starter.app;

import com.uber.cadence.client.starter.annotations.Activity;
import com.uber.cadence.client.starter.annotations.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Workflow("one")
@RequiredArgsConstructor
public class HelloWorkflowImpl implements HelloWorkflow {

  @Activity("ac")
  private final SimpleService simpleService;

  @Override
  public String process() {
    return "Hello from " + this.getClass() + " with uuid - " + simpleService.simpleWork();
  }

}
