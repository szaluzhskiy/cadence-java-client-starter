package com.uber.cadence.client.starter.app;

import org.springframework.stereotype.Service;

@Service
public class SimpleService {

  public String foo(String greeting) {
    return "Greeting from foo service - " + greeting;
  }

}
