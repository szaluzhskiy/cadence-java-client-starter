package com.uber.cadence.client.starter.app;


import com.uber.cadence.client.starter.annotations.EnableCadence;
import com.uber.cadence.client.starter.workflow.HelloWorkflow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableCadence
public class AutoConfigurationTest {

  @Autowired
  HelloWorkflow workflow;


  @Test
  public void contextLoads() {
    workflow.process();

  }
}
