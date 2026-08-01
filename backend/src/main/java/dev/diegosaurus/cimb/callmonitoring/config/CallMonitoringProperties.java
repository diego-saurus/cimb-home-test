package dev.diegosaurus.cimb.callmonitoring.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@ConfigurationProperties(prefix = "cimb.callmonitoring")
@Validated
public class CallMonitoringProperties {

    @Min(1)
    private int defaultPageSize = 5;

    private int maxPageSize = 200;

}
