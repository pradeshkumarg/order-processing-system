package com.pradesh.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    // Spring Boot auto-configuration will handle most of the Kafka producer configuration
    // based on the properties in application.yml
}
