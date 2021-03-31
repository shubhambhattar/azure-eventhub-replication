package com.example.ehreplication.config.immutableconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@AllArgsConstructor
@ToString(exclude = "connectionString")
@ConfigurationProperties(prefix = "producer")
public class ProducerConfig {

    private final String connectionString;
    private final int maximumSizeInBytes;
}
