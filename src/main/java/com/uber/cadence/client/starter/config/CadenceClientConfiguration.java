package com.uber.cadence.client.starter.config;

import com.uber.cadence.client.starter.annotations.EnableCadenceClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({EnableCadenceClient.class})
public class CadenceClientConfiguration {

}
