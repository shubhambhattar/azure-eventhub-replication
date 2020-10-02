package com.example.ehreplication.impl;

import java.util.function.Consumer;

import com.azure.messaging.eventhubs.models.InitializationContext;
import com.example.ehreplication.metrics.ConsumerMetrics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class ProcessPartitionInitialization implements Consumer<InitializationContext> {

    private final ConsumerMetrics consumerMetrics;

    @Bean
    public ProcessPartitionInitialization getPartitionInitialization(final ConsumerMetrics consumerMetrics) {
        return new ProcessPartitionInitialization(consumerMetrics);
    }


    @Override
    public void accept(InitializationContext initializationContext) {

        final String partitionId = initializationContext.getPartitionContext().getPartitionId();
        consumerMetrics.markPartitionInitialization(partitionId);

        log.info("Partition: {} initialized", partitionId);
    }
}
