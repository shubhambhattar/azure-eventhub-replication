package com.example.ehreplication.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ConsumerMetrics {

    private final MeterRegistry meterRegistry;

    @Bean
    public ConsumerMetrics getConsumerMetrics(final MeterRegistry meterRegistry) {
        return new ConsumerMetrics(meterRegistry);
    }

    public void markEvents(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.processEvent",
                        "partitionId", partitionId
                ).increment();
    }

    public void markPartitionInitialization(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.partitionInitialization",
                        "partitionId", partitionId
                ).increment();
    }

    public void markProcessError(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.processError",
                        "partitionId", partitionId
                ).increment();
    }

    public void markPartitionClose(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.partitionClose",
                        "partitionId", partitionId
                ).increment();
    }
}
