package com.uber.cadence.client.starter.app;

import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class SimpleServiceImpl implements SimpleService {

  @SneakyThrows
  @Override
  public UUID simpleWork() {
    Thread.sleep(1000);
    return UUID.randomUUID();
  }

}
