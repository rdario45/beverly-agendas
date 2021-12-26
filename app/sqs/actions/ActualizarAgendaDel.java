package sqs.actions;

import acl.BeverlyAction;
import domain.Agenda;
import domain.Cita;
import org.joda.time.DateTime;
import play.libs.Json;
import service.AgendasService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActualizarAgendaDel implements BeverlyAction {

    private AgendasService agendasService;

    @Inject
    public ActualizarAgendaDel(AgendasService agendasService) {
        this.agendasService = agendasService;
    }

    @Override
    public void execute(Object[] args) {
        Cita cita = Json.fromJson(Json.parse((String) args[0]), Cita.class);
        String hora = cita.getHora();
        DateTime dateTime = new DateTime(Long.parseLong(hora));
        long fecha = dateTime.toLocalDate().toDate().toInstant().toEpochMilli();

        Optional<Agenda> agendaFound = agendasService.findFirstAgenda(cita.getAgenda(),  Long.toString(fecha) );
        if (agendaFound.isPresent()) {
            agendaFound.ifPresent(agenda -> {
                List<Cita> collect = agenda.getCitas().stream()
                        .filter(cita1 -> !cita1.getId().equals(cita.getId()))
                        .collect(Collectors.toList());
                agenda.setCitas(collect);
                agendasService.update(agenda, agenda.getId());
            });
        }
    }
}