package sqs.actions;

import acl.types.BeverlyAction;
import domain.Agenda;
import domain.Cita;
import org.joda.time.DateTime;
import play.libs.Json;
import service.AgendasService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;

public class DisminuirAgenda implements BeverlyAction {

    private AgendasService agendasService;

    @Inject
    public DisminuirAgenda(AgendasService agendasService) {
        this.agendasService = agendasService;
    }

    @Override
    public void execute(Object[] args) {
        Cita cita = Json.fromJson(Json.parse((String) args[0]), Cita.class);
        String hora = cita.getHora();
        DateTime dateTime = new DateTime(Long.parseLong(hora));
        long fecha = dateTime.toLocalDate().toDate().toInstant().toEpochMilli();

        Optional<Agenda> agendaFound = agendasService.findFirstAgenda(cita.getAgenda(), Long.toString(fecha));

        if (agendaFound.isPresent()) {

            agendaFound.ifPresent(agenda -> {

                agenda.setCitas(agenda.getCitas().stream()
                        .filter(compare -> !compare.getId().equals(cita.getId()))
                        .collect(Collectors.toList()));;

                agendasService.update(agenda, agenda.getId());
            });
        }
    }
}