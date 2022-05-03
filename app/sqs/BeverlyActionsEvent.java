package sqs;

import acl.types.BeverlyAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import sqs.actions.ActualizarAgenda;
import sqs.actions.DisminuirAgenda;
import sqs.events.CitaActualizada;
import sqs.events.CitaCreada;
import sqs.events.CitaEliminada;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class BeverlyActionsEvent {

    private Map<String, List<BeverlyAction>> actions = new HashMap<>();

    @Inject
    public BeverlyActionsEvent(ActualizarAgenda actualizarAgenda,
                               DisminuirAgenda disminuirAgenda) {
        actions.put(CitaCreada.class.getSimpleName(), Arrays.asList(actualizarAgenda));
        actions.put(CitaActualizada.class.getSimpleName(), Arrays.asList(actualizarAgenda));
        actions.put(CitaEliminada.class.getSimpleName(), Arrays.asList(disminuirAgenda));
    }

    public void update(String eventName, Object... args) {
        actions.get(eventName).stream().forEach(beverlyAction -> {
            beverlyAction.execute(args);
        });
    }
}
