package com.example.ehreplication.impl;

import java.util.function.Consumer;

import com.azure.messaging.eventhubs.models.CloseContext;
import com.example.ehreplication.metrics.ConsumerMetrics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PartitionClose implements Consumer<CloseContext> {

    private final ConsumerMetrics consumerMetrics;

    @Override
    public void accept(CloseContext closeContext) {

        final String partitionId = closeContext.getPartitionContext().getPartitionId();
        consumerMetrics.markPartitionClose(partitionId);

        log.info("Partition: {} closed for reason: {}", partitionId, closeContext.getCloseReason());
    }
}
