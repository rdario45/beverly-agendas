package mapper;

import acl.DynamoMapper;
import domain.Agenda;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgendaMapper implements DynamoMapper<Agenda> {

    public Agenda map(Map<String, AttributeValue> map) {
        return new Agenda(
                Optional.ofNullable(map.get("id")).map(AttributeValue::s).orElse("undefined"),
                Optional.ofNullable(map.get("manicurista")).map(AttributeValue::s).orElse("undefined"),
                Optional.ofNullable(map.get("fecha")).map(AttributeValue::n).orElse("0"),
                Optional.ofNullable(map.get("citas")).map(AttributeValue::l)
                .map(attributeValues -> new CitaMapper().map(attributeValues))
                .orElse(Collections.emptyList())
        );
    }

    @Override
    public List<Agenda> map(List<AttributeValue> list) {
        return list.stream().map(AttributeValue::m).map(this::map).collect(Collectors.toList());
    }
}
