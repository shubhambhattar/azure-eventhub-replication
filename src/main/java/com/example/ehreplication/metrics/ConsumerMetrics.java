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
    private final Map<String, AtomicLong> messageTimestamp;

    public ConsumerMetrics(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        lags = new HashMap<>();
        messageTimestamp = new HashMap<>();
    }

    @Bean
    public ConsumerMetrics getConsumerMetrics(final MeterRegistry meterRegistry) {
        return new ConsumerMetrics(meterRegistry);
    }

    public void markEvents(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer_process_event",
                        Tags.of("partition", partitionId)
                ).increment();
    }

    public void markPartitionInitialization(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer_partition_initialization",
                        Tags.of("partition", partitionId)
                ).increment();
    }

    public void markProcessError(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer_process_error",
                        Tags.of("partition", partitionId)
                ).increment();
    }

    public void markPartitionClose(final String partitionId) {

        meterRegistry
                .counter(
                        "consumer_partition_close",
                        Tags.of("partition", partitionId)
                ).increment();
    }

    public void updateLag(final String partitionId, final long lag) {

        if (lags.containsKey(partitionId)) {
            lags.get(partitionId).set(lag);
        } else {
            lags.put(
                    partitionId,
                    meterRegistry.gauge(
                            "consumer_lag",
                            Tags.of("partition", partitionId),
                            new AtomicLong(lag)
                    )
            );
        }
    }

    public void updateMessageTimestamp(final String partitionId, final long timestamp) {

        if (messageTimestamp.containsKey(partitionId)) {
            messageTimestamp.get(partitionId).set(timestamp);
        } else {
            messageTimestamp.put(
                    partitionId,
                    meterRegistry.gauge(
                            "consumer_message_timestamp",
                            Tags.of("partition", partitionId),
                            new AtomicLong(timestamp)
                    )
            );
        }
    }
}
