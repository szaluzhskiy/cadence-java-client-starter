package com.uber.cadence.client.starter.app.workflow;

import com.uber.cadence.client.starter.annotations.Workflow;
import com.uber.cadence.workflow.WorkflowMethod;

@Workflow("one")
public interface HelloWorkflow {

  @WorkflowMethod
  void process();
}
