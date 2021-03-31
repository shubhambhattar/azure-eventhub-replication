package com.example.ehreplication.impl;

import java.util.function.Consumer;

import com.azure.messaging.eventhubs.models.ErrorContext;
import com.example.ehreplication.metrics.ConsumerMetrics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProcessError implements Consumer<ErrorContext> {

    private final ConsumerMetrics consumerMetrics;

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
