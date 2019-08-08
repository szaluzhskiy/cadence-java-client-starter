package com.uber.cadence.client.starter.app;

import com.uber.cadence.client.starter.RegisterDomain;
import com.uber.cadence.client.starter.annotations.EnableCadence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCadence
@SpringBootApplication(exclude = {RegisterDomain.class})
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
