package com.insight.uploadclean.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CLEANED_DATA_QUEUE = "cleaned-data-queue";

    @Bean
    public Queue cleanedDataQueue() {
        return new Queue(CLEANED_DATA_QUEUE, true);
    }
}

