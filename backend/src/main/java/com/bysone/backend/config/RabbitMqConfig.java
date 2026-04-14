package com.bysone.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NOTIFICACIONES = "bysone.notificaciones";
    public static final String QUEUE_EMAIL             = "bysone.email.notificacion";
    public static final String ROUTING_KEY_EMAIL       = "email.calibracion.completada";

    @Bean
    public TopicExchange exchangeNotificaciones() {
        return new TopicExchange(EXCHANGE_NOTIFICACIONES, true, false);
    }

    @Bean
    public Queue queueEmail() {
        return QueueBuilder.durable(QUEUE_EMAIL).build();
    }

    @Bean
    public Binding bindingEmail(Queue queueEmail, TopicExchange exchangeNotificaciones) {
        return BindingBuilder.bind(queueEmail).to(exchangeNotificaciones).with(ROUTING_KEY_EMAIL);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
