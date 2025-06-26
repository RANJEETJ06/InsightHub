package com.insight.analysisinsights.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CLEANED_DATA_QUEUE = "cleaned-data-queue";

    @Value("${insight.rabbitmq.queue}")
    private String queue;

    @Value("${insight.rabbitmq.exchange}")
    private String exchange;

    @Value("${insight.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue insightQueue() {
        return new Queue(queue);
    }

    @Bean
    public TopicExchange insightExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue insightQueue, TopicExchange insightExchange) {
        return BindingBuilder.bind(insightQueue).to(insightExchange).with(routingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Value("${report.rabbitmq.exchange}")
    private String reportExchange;

    @Value("${report.rabbitmq.routing-key}")
    private String reportRoutingKey;

    @Bean
    public TopicExchange reportExchange() {
        return new TopicExchange(reportExchange);
    }

}
