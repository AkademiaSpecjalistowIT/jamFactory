package pl.akademiaspecjalistowit.jamfactory.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "limits")
@Component
public class ApiProperties {
    private Integer maxDeliveryCapacity;
    private Integer maxProductionLimit;
}