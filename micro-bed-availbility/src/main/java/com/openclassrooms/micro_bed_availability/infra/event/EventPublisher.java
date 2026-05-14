package com.openclassrooms.micro_bed_availability.infra.event;

import com.openclassrooms.micro_bed_availability.domain.fetch.IPublishEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher implements IPublishEvent {
    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(AppointmentEvent event) {
        rabbitTemplate.convertAndSend("app.exchange", "appointment.events", event);
    }
}
