package com.example.ehreplication.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class ConsumerMetrics {

    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicLong> lags;

    public ConsumerMetrics(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        lags = new HashMap<>();
    }

    @Bean
    public ConsumerMetrics getConsumerMetrics(final MeterRegistry meterRegistry) {
        return new ConsumerMetrics(meterRegistry);
    }

    public void markEvents(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.processEvent",
                        Tags.of("partitionId", partitionId)
                ).increment();
    }

    public void markPartitionInitialization(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.partitionInitialization",
                        Tags.of("partitionId", partitionId)
                ).increment();
    }

    public void markProcessError(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.processError",
                        Tags.of("partitionId", partitionId)
                ).increment();
    }

    public void markPartitionClose(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer.partitionClose",
                        Tags.of("partitionId", partitionId)
                ).increment();
    }

    public void updateLag(final String partitionId, final long lag) {

        if (lags.containsKey(partitionId)) {
            lags.get(partitionId).set(lag);
        } else {
            lags.put(
                    partitionId,
                    meterRegistry.gauge(
                            "consumer.lag",
                            Tags.of("partitionId", partitionId),
                            new AtomicLong(lag)
                    )
            );
        }
    }
}
