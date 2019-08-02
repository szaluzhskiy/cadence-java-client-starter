package com.uber.cadence.client.starter.app;

import com.uber.cadence.client.starter.annotations.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Workflow("one")
@RequiredArgsConstructor
public class HelloWorkflowImpl implements HelloWorkflow {

  private final SimpleService simpleService;

  @Override
  public void process() {
    System.out.println(
        "Hello from " + this.getClass() + " including " + simpleService.getClass()
    );
  }
}
