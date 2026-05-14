package com.openclassrooms.microservice_appointment.infra.event;

import com.openclassrooms.microservice_appointment.infra.entity.Appointment;
import com.openclassrooms.microservice_appointment.infra.repository.AppointmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

    @Autowired
    AppointmentRepository appointmentRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("appointment.queue"),
            exchange = @Exchange("app.exchange"),
            key = "appointment.events"
    ))

    public void handle(AppointmentEvent event) {
        log.info("Événement reçu : " + event);
        // Traiter l'événement ici

        Appointment appointment=new Appointment();
        //appointment.setHospital();

    }
}