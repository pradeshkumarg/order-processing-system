package com.pradesh.notificationservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class KafkaConfigTest {

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Test
    void consumerFactory_ShouldBeConfiguredCorrectly() {
        // Given
        Map<String, Object> configs = consumerFactory.getConfigurationProperties();

        // Then
        assertNotNull(configs);
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertTrue(configs.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertTrue(configs.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
        assertTrue(configs.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
    }

    @Test
    void kafkaListeners_ShouldBeRegistered() {
        // Given
        MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer("inventory-updated-listener");
        MessageListenerContainer failedContainer = kafkaListenerEndpointRegistry.getListenerContainer("inventory-failed-listener");

        // Then
        assertNotNull(container);
        assertNotNull(failedContainer);
        assertTrue(container.isRunning());
        assertTrue(failedContainer.isRunning());
    }
} 