package com.uber.cadence.client.starter.app;

import com.uber.cadence.workflow.WorkflowMethod;

public interface HelloWorkflow {

  @WorkflowMethod
  void process();
}
