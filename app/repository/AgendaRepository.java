package repository;

import acl.BeverlyDynamoDB;
import domain.Agenda;
import domain.Cita;
import mapper.AgendaMapper;
import mapper.CitaMapper;
import org.joda.time.DateTime;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgendaRepository {

    public Agenda save(Agenda agenda) {
        return BeverlyDynamoDB.putItem("agendas", agenda);
    }

    public Optional<Agenda> findFirst(String id) {

        long millisInit = new DateTime("2022-01-01T05:00:00.000-05:00").getMillis();
        long millisEnd = new DateTime("2022-12-31T05:00:00.000-05:00").getMillis();

        HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":id", AttributeValue.builder().s(id).build());
        values.put(":fechaInicial", AttributeValue.builder().n("" + millisInit).build());
        values.put(":fechaFinal", AttributeValue.builder().n("" + millisEnd).build());
        return BeverlyDynamoDB.getFirst("agendas", "id = :id AND fecha BETWEEN :fechaInicial AND :fechaFinal", values)
                .map(valueMap -> new AgendaMapper().map(valueMap));
    }

    public Optional<Agenda> findFirstAgendaByFecha(String agenda, String fecha) {
        HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":manicurista", AttributeValue.builder().s(agenda).build());
        values.put(":fecha", AttributeValue.builder().n(fecha).build());
        return BeverlyDynamoDB.getFirst("agendas", "manicurista = :manicurista AND fecha = :fecha", values)
                .map(valueMap -> new AgendaMapper().map(valueMap));
    }

    public List<Agenda> findByRange(String startDate, String finalDate) {
        HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":fechaInicial", AttributeValue.builder().n(startDate).build());
        values.put(":fechaFinal", AttributeValue.builder().n(finalDate).build());
        return BeverlyDynamoDB.getAll("agendas", "fecha BETWEEN :fechaInicial and :fechaFinal", values)
                .stream().map(valueMap -> new AgendaMapper().map(valueMap))
                .collect(Collectors.toList());
    }

    public List<Cita> getAll() {
        HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":now", AttributeValue.builder().n("" + new DateTime().getMillis()).build());
        return BeverlyDynamoDB.getAll("agendas", "fecha < :now", values)
                .stream().map(valueMap -> new CitaMapper().map(valueMap))
                .collect(Collectors.toList());
    }
}
