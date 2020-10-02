package com.example.ehreplication.immutableconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@AllArgsConstructor
@ConfigurationProperties(prefix = "producer")
public class ProducerConfig {

    private final String connectionString;
    private final int maximumSizeInBytes;
}
