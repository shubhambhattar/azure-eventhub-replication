package com.example.ehreplication;

import com.azure.messaging.eventhubs.EventProcessorClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@AllArgsConstructor
@SpringBootApplication
@ConfigurationPropertiesScan
public class EhReplicationApplication implements CommandLineRunner {

	private final EventProcessorClient eventProcessorClient;

	public static void main(String[] args) {
		SpringApplication.run(EhReplicationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		eventProcessorClient.start();
	}
}
