package com.uber.cadence.client.starter.app;

import org.springframework.stereotype.Service;

@Service
public class SimpleService {

  public String sayHello() {
    System.out.println("!!!!!!!!!!!!!!!! SimpleService !!!!!!!!!!!!!!!!!!");
    return "Hello from SimpleService";
  }

}
