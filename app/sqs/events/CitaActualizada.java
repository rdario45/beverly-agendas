package sqs.events;

import acl.BeverlyMsg;
import domain.Cita;

public class CitaActualizada extends BeverlyMsg {
    public CitaActualizada(Cita cita) {
        super(cita, CitaActualizada.class.getSimpleName());
    }
}
