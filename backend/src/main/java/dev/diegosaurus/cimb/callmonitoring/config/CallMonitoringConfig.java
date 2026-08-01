package dev.diegosaurus.cimb.callmonitoring.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CallMonitoringProperties.class)
public class CallMonitoringConfig {
}
