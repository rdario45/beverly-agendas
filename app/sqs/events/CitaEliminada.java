package sqs.events;

import acl.types.BeverlyEvent;
import domain.Cita;

public class CitaEliminada extends BeverlyEvent {

    public CitaEliminada(Cita cita) {
        super(CitaEliminada.class.getSimpleName(), cita);
    }
}