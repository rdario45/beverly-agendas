package repository;

import acl.BeverlyDB;
import domain.Agenda;
import mapper.AgendaMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgendaRepository {

    public Optional<Agenda> find(String id) {
        return BeverlyDB.getItem("agendas", "id", id)
                .map(valueMap -> new AgendaMapper().map(valueMap));
    }

    public List<Agenda> findAll() {
        return BeverlyDB.getAll("agendas").stream()
                .map(valueMap -> new AgendaMapper().map(valueMap))
                .collect(Collectors.toList());
    }

    public Agenda save(Agenda agenda) {
        return BeverlyDB.putItem("agendas", agenda);
    }

    public Optional<Agenda> findByName(String agenda) {
        HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":manicurista", AttributeValue.builder().s(agenda).build());
        return BeverlyDB.getFirst("agendas", "manicurista = :manicurista", values)
                .map(valueMap -> new AgendaMapper().map(valueMap));
    }
}
