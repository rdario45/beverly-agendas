package sqs.events;

import acl.types.BeverlyEvent;
import domain.Cita;

public class CitaCreada extends BeverlyEvent {

    public CitaCreada(Cita cita) {
        super(CitaCreada.class.getSimpleName(), cita);
    }
}