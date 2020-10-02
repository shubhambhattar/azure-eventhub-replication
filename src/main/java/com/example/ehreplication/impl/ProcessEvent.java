package com.example.ehreplication.impl;

import java.util.function.Consumer;

import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.azure.messaging.eventhubs.models.EventContext;
import com.example.ehreplication.metrics.ConsumerMetrics;
import com.example.ehreplication.metrics.ProducerMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProcessEvent implements Consumer<EventContext> {

    private final EventHubProducerClient eventHubProducerClient;
    private final CreateBatchOptions createBatchOptions;
    private final ConsumerMetrics consumerMetrics;
    private final ProducerMetrics producerMetrics;

    private final ThreadLocal<EventDataBatch> eventDataBatchThreadLocal;

    public ProcessEvent(final EventHubProducerClient eventHubProducerClient,
                        final CreateBatchOptions createBatchOptions,
                        final ConsumerMetrics consumerMetrics,
                        final ProducerMetrics producerMetrics) {

        this.eventHubProducerClient = eventHubProducerClient;
        this.createBatchOptions = createBatchOptions;
        this.consumerMetrics = consumerMetrics;
        this.producerMetrics = producerMetrics;

        this.eventDataBatchThreadLocal = ThreadLocal.withInitial(
                () -> eventHubProducerClient.createBatch(createBatchOptions)
        );
    }

    @Bean
    public ProcessEvent getProcessEvent(final EventHubProducerClient eventHubProducerClient,
                                        final CreateBatchOptions createBatchOptions,
                                        final ConsumerMetrics consumerMetrics,
                                        final ProducerMetrics producerMetrics) {

        return new ProcessEvent(eventHubProducerClient, createBatchOptions, consumerMetrics, producerMetrics);
    }

    @Override
    public void accept(EventContext eventContext) {

        final String partitionId = eventContext.getPartitionContext().getPartitionId();

        consumerMetrics.markEvents(partitionId);

        if (!eventDataBatchThreadLocal.get().tryAdd(eventContext.getEventData())) {

            eventHubProducerClient.send(eventDataBatchThreadLocal.get());

            producerMetrics.markEvents(eventDataBatchThreadLocal.get().getCount());
            producerMetrics.markEventsSize(eventDataBatchThreadLocal.get().getSizeInBytes());

            eventDataBatchThreadLocal.set(eventHubProducerClient.createBatch(createBatchOptions));

            if (!eventDataBatchThreadLocal.get().tryAdd(eventContext.getEventData())) {

                producerMetrics.markMessageTooBig();
                log.error(
                        "Message size {} too big to fit in one EventDataBatch.",
                        eventContext.getEventData().getBody().length
                );
            }
        }
    }
}
