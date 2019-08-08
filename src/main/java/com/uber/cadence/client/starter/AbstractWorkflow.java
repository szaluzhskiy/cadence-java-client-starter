package com.uber.cadence.client.starter;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import com.uber.cadence.client.WorkflowOptions.Builder;
import com.uber.cadence.client.starter.config.CadenceProperties.WorkflowOption;
import java.time.Duration;
import lombok.RequiredArgsConstructor;

public class AbstractWorkflow<E, I> {

  @RequiredArgsConstructor
  public class WorkflowFactory<E, I> {

    private final WorkflowClient workflowClient;
    private final WorkflowOption option;
    private final String key;
    private final Class<E> clazzInterface;

    public E next() {
      WorkflowOptions options = new Builder()
          .setTaskList(key)
          .setExecutionStartToCloseTimeout(
              Duration.ofSeconds(option.getExecutionTimeout()))
          .build();

      return workflowClient.newWorkflowStub(clazzInterface, options);
    }
  }
}
