package sqs.actions;

import acl.BeverlyAction;
import domain.Agenda;
import domain.Cita;
import org.joda.time.DateTime;
import play.libs.Json;
import service.AgendasService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class ActualizarAgenda implements BeverlyAction {

    private AgendasService agendasService;

    @Inject
    public ActualizarAgenda(AgendasService agendasService) {
        this.agendasService = agendasService;
    }

    @Override
    public void execute(Object[] args) {
        Cita cita = Json.fromJson(Json.parse((String) args[0]), Cita.class);
        String hora = cita.getHora();
        DateTime dateTime = new DateTime(Long.parseLong(hora));
        long fecha = dateTime.toLocalDate().toDate().toInstant().toEpochMilli();

        Optional<Agenda> agendaFound = agendasService.findFirstAgenda(cita.getAgenda(), String.valueOf(fecha));

        if (agendaFound.isPresent()) {
            agendaFound.ifPresent(agenda -> {
                Optional<Cita> any = agenda.getCitas().stream()
                        .filter(x -> x.getId().equals(cita.getId())).findAny();

                if (any.isPresent()) {
                    agenda.getCitas().stream().map(x -> x.equals(cita) ? x.update(cita) : x);
                } else {
                    agenda.getCitas().add(cita);
                }
                agendasService.update(agenda, agenda.getId());
            });
        } else {
            Agenda agenda = new Agenda(UUID.randomUUID().toString(), cita.getAgenda(), String.valueOf(fecha), Collections.singletonList(cita));
            agendasService.save(agenda);
        }
    }
}