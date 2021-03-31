package com.example.ehreplication.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.example.ehreplication.config.immutableconfig.ProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EHProducerConfig {

    @Bean(destroyMethod = "close")
    public EventHubProducerClient getEventHubProducerClient(final ProducerConfig producerConfig) {

        log.info("--- EventHub Producer Configuration ---");
        log.info(producerConfig.toString());
        log.info("---------------------------------------");
        
        return new EventHubClientBuilder()
                .connectionString(producerConfig.getConnectionString())
                .buildProducerClient();
    }


    @Bean(destroyMethod = "close")
    public EventHubProducerAsyncClient getEventHubProducerAsyncClient(final ProducerConfig producerConfig) {

        log.info("--- EventHub Producer Configuration ---");
        log.info(producerConfig.toString());
        log.info("---------------------------------------");

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
