package com.example.ehreplication.controller;

import com.azure.messaging.eventhubs.EventProcessorClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventProcessorClientHealthIndicator implements HealthIndicator {

    private final EventProcessorClient eventProcessorClient;

    @Override
    public Health health() {
        if (eventProcessorClient.isRunning()) {
            return Health.up().build();
        }
        return Health.down().withDetail("EventProcessorClient is not running", "").build();
    }
}
