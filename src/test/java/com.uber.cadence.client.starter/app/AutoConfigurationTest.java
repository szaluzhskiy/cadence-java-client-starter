package com.uber.cadence.client.starter.app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AutoConfigurationTest {

  @Test
  public void contextLoads() {

    assertTrue(true);

  }
}
