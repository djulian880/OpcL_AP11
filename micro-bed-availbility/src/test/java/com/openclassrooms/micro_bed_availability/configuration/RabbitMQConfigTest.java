package com.openclassrooms.micro_bed_availability.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void shouldCreateJsonMessageConverter() {
        MessageConverter converter = config.jsonMessageConverter();
        assertNotNull(converter);
    }

    @Test
    void shouldCreateRabbitTemplate() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        MessageConverter converter = mock(MessageConverter.class);

        RabbitTemplate template = config.rabbitTemplate(connectionFactory, converter);

        assertNotNull(template);
        assertEquals(converter, template.getMessageConverter());
    }
}