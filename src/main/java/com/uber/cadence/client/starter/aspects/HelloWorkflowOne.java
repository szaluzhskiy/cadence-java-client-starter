package com.uber.cadence.client.starter.aspects;

import com.uber.cadence.client.starter.annotations.Workflow;
import com.uber.cadence.workflow.WorkflowMethod;

@Workflow("one")
public interface HelloWorkflowOne {

  @WorkflowMethod
  public void process();
}
