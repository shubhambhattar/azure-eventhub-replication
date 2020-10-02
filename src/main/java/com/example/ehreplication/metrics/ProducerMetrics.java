package com.example.ehreplication.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ProducerMetrics {

    private final MeterRegistry meterRegistry;

    @Bean
    public ProducerMetrics getProducerMetrics(final MeterRegistry meterRegistry) {
        return new ProducerMetrics(meterRegistry);
    }

    public void markEvents(final int count) {

        meterRegistry.counter("producer.events").increment(count);
    }

    public void markEventsSize(final int size) {

        meterRegistry.counter("producer.eventsSize").increment(size);
    }

    public void markMessageTooBig() {

        meterRegistry.counter("producer.msgTooBigError").increment();
    }
}
