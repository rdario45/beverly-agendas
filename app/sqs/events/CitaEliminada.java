package sqs.events;

import acl.BeverlyMsg;
import domain.Cita;

public class CitaEliminada extends BeverlyMsg {
    public CitaEliminada(Cita cita) {
        super(cita, CitaEliminada.class.getSimpleName());
    }
}
