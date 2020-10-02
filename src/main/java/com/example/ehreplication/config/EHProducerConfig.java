package com.example.ehreplication.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.example.ehreplication.immutableconfig.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EHProducerConfig {

    @Bean
    public EventHubProducerClient getEventHubProducerClient(final ProducerConfig producerConfig) {

        return new EventHubClientBuilder()
                .connectionString(producerConfig.getConnectionString())
                .buildProducerClient();
    }


    @Bean
    public EventHubProducerAsyncClient getEventHubProducerAsyncClient(final ProducerConfig producerConfig) {

        return new EventHubClientBuilder()
                .connectionString(producerConfig.getConnectionString())
                .buildAsyncProducerClient();
    }

    @Bean
    public CreateBatchOptions getCreateBatchOptions(final ProducerConfig producerConfig) {

        return new CreateBatchOptions()
                .setMaximumSizeInBytes(producerConfig.getMaximumSizeInBytes());
    }
}
