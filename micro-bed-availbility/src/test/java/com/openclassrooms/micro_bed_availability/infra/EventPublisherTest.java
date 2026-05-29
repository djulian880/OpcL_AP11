package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;
import com.openclassrooms.micro_bed_availability.infra.event.EventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

class EventPublisherTest {

    @Test
    void shouldPublishEvent() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

        EventPublisher publisher = new EventPublisher(rabbitTemplate);

        AppointmentEvent event = new AppointmentEvent(
                "Hospital A",
                "CARDIO",
                "booking",
                "2026-01-01"
        );

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
                "app.exchange",
                "appointment.events",
                event
        );
    }
}
