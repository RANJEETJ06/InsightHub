package com.insight.report.config;

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

    @Value("${report.rabbitmq.queue}")
    private String queue;

    @Value("${report.rabbitmq.exchange}")
    private String exchange;

    @Value("${report.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue reportQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public TopicExchange reportExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding reportBinding(Queue reportQueue, TopicExchange reportExchange) {
        return BindingBuilder.bind(reportQueue).to(reportExchange).with(routingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
