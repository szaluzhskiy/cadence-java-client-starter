package com.uber.cadence.client.starter.app;


import com.uber.cadence.client.starter.WorkflowFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AutoConfigurationTest {

  @Autowired
  WorkflowFactory<HelloWorkflow, HelloWorkflowImpl> factory;
  @Autowired
  WorkflowFactory<HelloWorkflow, HelloWorkflowImplTwo> factoryTwo;

  @Test
  public void contextLoads() {

    factory.next().process();

    factoryTwo.next().process();

  }
}
