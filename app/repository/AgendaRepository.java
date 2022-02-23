package repository;

import acl.BeverlyDynamoDB;
import domain.Agenda;
import mapper.AgendaMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgendaRepository {

    public Optional<Agenda> find(String id) {
        return BeverlyDynamoDB.getItem("agendas", "id", id)
                .map(valueMap -> new AgendaMapper().map(valueMap));
    }

    public Agenda save(Agenda agenda) {
        return BeverlyDynamoDB.putItem("agendas", agenda);
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
}
