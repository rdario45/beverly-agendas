package sqs.events;

import acl.BeverlyMsg;
import domain.Cita;

public class CitaCreada extends BeverlyMsg {
    public CitaCreada(Cita cita) {
        super(cita, CitaCreada.class.getSimpleName());
    }
}
