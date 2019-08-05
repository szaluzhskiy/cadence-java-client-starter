package com.uber.cadence.client.starter.app;


import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.starter.app.workflow.HelloWorkflow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AutoConfigurationTest {

  @Autowired
  WorkflowClient workflowClient;


  @Test
  public void contextLoads() {
    HelloWorkflow workflow = workflowClient
        .newWorkflowStub(HelloWorkflow.class);

    workflow.process();

  }
}
