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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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
        appointment.setHospital(event.hospitalName());
        appointment.setSpeciality(event.speciality());

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

// Conversion
        //Date date = Date.parse(event.date(), "yyyy-MM-dd");
        try{
            Date date = formatter.parse(event.date());
            appointment.setEntranceDate(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

// Étape 3 : Ajout de 7 jours
            calendar.add(Calendar.DAY_OF_MONTH, 7);

// Étape 4 : Récupération de la nouvelle date
            Date nouvelleDate = calendar.getTime();
            appointment.setLeavingDate(nouvelleDate);
            appointment.setFirstName("Pierre");
            appointment.setLastName("Poljak");
            log.info("Nouveau RDV enregistré: "+appointment.toString());
;            appointmentRepository.save(appointment);

        } catch (ParseException e) {
            log.error(e.getMessage());
        }


    }


}