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
        producedEvents = meterRegistry.counter("producer_events");
        eventsSize = meterRegistry.gauge("producer_events_size", new AtomicInteger(0));
        msgTooBigError = meterRegistry.counter("producer_msg_too_big_error");
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
