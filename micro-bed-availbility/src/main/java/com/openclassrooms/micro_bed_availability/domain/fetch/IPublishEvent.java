package com.openclassrooms.micro_bed_availability.domain.fetch;

import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;

public interface IPublishEvent {
    void publish(AppointmentEvent event);
}
