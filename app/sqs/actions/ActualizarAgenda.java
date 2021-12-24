package sqs.actions;

import acl.BeverlyAction;
import domain.Agenda;
import domain.Cita;
import play.libs.Json;
import service.AgendasService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActualizarAgenda implements BeverlyAction {

    private AgendasService agendasService;

    @Inject
    public ActualizarAgenda(AgendasService agendasService) {
        this.agendasService = agendasService;
    }

    @Override
    public void execute(Object[] args) {
        Cita cita = Json.fromJson(Json.parse((String) args[0]), Cita.class);
        Optional<Agenda> agendaFound = agendasService.findFirst(cita.getAgenda());
        if (agendaFound.isPresent()) {
            agendaFound.ifPresent(agenda -> {
                if (agenda.getCitas().stream().anyMatch(cita1 -> cita1.equals(cita))) {
                    agenda.setCitas(agenda.getCitas().stream()
                            .map(cita1 -> cita1.equals(cita) ? cita1.update(cita) : cita1)
                            .collect(Collectors.toList()));
                } else {
                    agenda.getCitas().add(cita);
                }
                agendasService.update(agenda, agenda.getId());
            });
        } else {
            Agenda agenda = new Agenda(UUID.randomUUID().toString(), cita.getAgenda(), Collections.singletonList(cita));
            agendasService.save(agenda);
        }
    }
}