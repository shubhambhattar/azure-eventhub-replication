package com.example.ehreplication.impl;

import java.util.function.Consumer;

import com.azure.messaging.eventhubs.models.ErrorContext;
import com.example.ehreplication.metrics.ConsumerMetrics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class ProcessError implements Consumer<ErrorContext> {

    private final ConsumerMetrics consumerMetrics;

    @Bean
    public ProcessError getProcessError(final ConsumerMetrics consumerMetrics) {
        return new ProcessError(consumerMetrics);
    }

    @Override
    public void accept(ErrorContext errorContext) {

        final String partitionId = errorContext.getPartitionContext().getPartitionId();
        consumerMetrics.markProcessError(partitionId);

        log.error(
                "Error while consuming from EventHub partition {}",
                partitionId,
                errorContext.getThrowable()
        );
    }
}
