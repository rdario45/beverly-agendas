package sqs.events;

import acl.types.BeverlyEvent;
import domain.Cita;

public class CitaActualizada extends BeverlyEvent {

    public CitaActualizada(Cita cita) {
        super(CitaActualizada.class.getSimpleName(), cita);
    }
}