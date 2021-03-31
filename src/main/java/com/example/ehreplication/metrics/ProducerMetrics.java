package com.example.ehreplication.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ProducerMetrics {

    private final AtomicInteger eventsSize;
    private final Counter producedEvents, msgTooBigError;

    public ProducerMetrics(final MeterRegistry meterRegistry) {
        producedEvents = meterRegistry.counter("producer.events");
        eventsSize = meterRegistry.gauge("producer.eventsSize", new AtomicInteger(0));
        msgTooBigError = meterRegistry.counter("producer.msgTooBigError");
    }

    public void markEvents(final int count) {
        producedEvents.increment(count);
    }

    public void markEventsSize(final int size) {
        eventsSize.set(size);
    }

    public void markMessageTooBig() {
        msgTooBigError.increment();
    }
}
