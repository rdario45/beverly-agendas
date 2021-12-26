package sqs;

import acl.BeverlyAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import sqs.actions.ActualizarAgenda;
import sqs.actions.ActualizarAgendaDel;
import sqs.events.CitaActualizada;
import sqs.events.CitaCreada;
import sqs.events.CitaEliminada;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ActionsEvent {

    private Map<String, List<BeverlyAction>> actions = new HashMap<>();

    @Inject
    public ActionsEvent(ActualizarAgenda actualizarAgenda,
                        ActualizarAgendaDel actualizarAgendaDel) {
        actions.put(CitaCreada.class.getSimpleName(), Arrays.asList(actualizarAgenda));
        actions.put(CitaActualizada.class.getSimpleName(), Arrays.asList(actualizarAgenda));
        actions.put(CitaEliminada.class.getSimpleName(), Arrays.asList(actualizarAgendaDel));
    }

    public void update(String eventName, Object... args) {
        actions.get(eventName).stream().forEach(beverlyAction -> {
            beverlyAction.execute(args);
        });
    }
}
