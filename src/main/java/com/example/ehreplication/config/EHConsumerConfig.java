package com.example.ehreplication.config;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.azure.messaging.eventhubs.CheckpointStore;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.example.ehreplication.immutableconfig.ConsumerConfig;
import com.example.ehreplication.impl.PartitionClose;
import com.example.ehreplication.impl.ProcessEvent;
import com.example.ehreplication.impl.ProcessPartitionInitialization;
import com.example.ehreplication.impl.ProcessError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EHConsumerConfig {

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
