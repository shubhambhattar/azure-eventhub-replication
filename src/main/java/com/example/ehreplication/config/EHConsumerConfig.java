package com.example.ehreplication.config;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.azure.messaging.eventhubs.CheckpointStore;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.example.ehreplication.config.immutableconfig.ConsumerConfig;
import com.example.ehreplication.impl.PartitionClose;
import com.example.ehreplication.impl.ProcessEvent;
import com.example.ehreplication.impl.ProcessPartitionInitialization;
import com.example.ehreplication.impl.ProcessError;
import com.example.ehreplication.metrics.ConsumerMetrics;
import com.example.ehreplication.metrics.ProducerMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EHConsumerConfig {

    @Bean
    public PartitionClose getPartitionClose(final ConsumerMetrics consumerMetrics) {
        return new PartitionClose(consumerMetrics);
    }

    @Bean
    public ProcessError getProcessError(final ConsumerMetrics consumerMetrics) {
        return new ProcessError(consumerMetrics);
    }

    @Bean
    public ProcessEvent getProcessEvent(final EventHubProducerClient eventHubProducerClient,
                                        final CreateBatchOptions createBatchOptions,
                                        final ConsumerMetrics consumerMetrics,
                                        final ProducerMetrics producerMetrics) {

        return new ProcessEvent(eventHubProducerClient, createBatchOptions, consumerMetrics, producerMetrics);
    }

    @Bean
    public ProcessPartitionInitialization getPartitionInitialization(final ConsumerMetrics consumerMetrics) {
        return new ProcessPartitionInitialization(consumerMetrics);
    }

    @Bean
    public Map<String, EventPosition> getInitialPartitionEventPosition(final ConsumerConfig consumerConfig) {

        Map<String, EventPosition> initialPartitionEventPosition = new HashMap<>();

        switch (consumerConfig.getInitialPartitionEventPosition()) {

            case "earliest": {

                for (int i = 0; i < consumerConfig.getNoOfPartitions(); i++) {
                    initialPartitionEventPosition.put(String.valueOf(i), EventPosition.earliest());
                }
                break;
            }

            case "latest": {

                for (int i = 0; i < consumerConfig.getNoOfPartitions(); i++) {
                    initialPartitionEventPosition.put(String.valueOf(i), EventPosition.latest());
                }
                break;
            }

            default: {

                Instant enqueuedTime = Instant.parse(consumerConfig.getInitialPartitionEventPosition());

                for (int i = 0; i < consumerConfig.getNoOfPartitions(); i++) {
                    initialPartitionEventPosition.put(String.valueOf(i), EventPosition.fromEnqueuedTime(enqueuedTime));
                }
                break;
            }
        }

        return initialPartitionEventPosition;
    }

    @Bean(destroyMethod = "stop")
    public EventProcessorClient getEventProcessorClient(final ConsumerConfig consumerConfig,
                                                        final Map<String, EventPosition> initialPartitionEventPosition,
                                                        final ProcessPartitionInitialization processPartitionInitialization,
                                                        final ProcessEvent processEvent,
                                                        final ProcessError processError,
                                                        final PartitionClose partitionClose,
                                                        final CheckpointStore checkpointStore) {

        log.info("--- EventHub Consumer Configuration ---");
        log.info(consumerConfig.toString());
        log.info("---------------------------------------");

        return new EventProcessorClientBuilder()
                .connectionString(consumerConfig.getConnectionString())
                .consumerGroup(consumerConfig.getConsumerGroup())
                .trackLastEnqueuedEventProperties(consumerConfig.isTrackLastEnqueuedEventProperties())
                .initialPartitionEventPosition(initialPartitionEventPosition)
                .processPartitionInitialization(processPartitionInitialization)
                .checkpointStore(checkpointStore)
                .processEvent(processEvent)
                .processError(processError)
                .processPartitionClose(partitionClose)
                .buildEventProcessorClient();
    }
}
