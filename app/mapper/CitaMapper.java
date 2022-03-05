package mapper;

import acl.BeverlyDynamoMapper;
import domain.Cita;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitaMapper implements BeverlyDynamoMapper<Cita> {

    public Cita map(Map<String, AttributeValue> map) {
        return new Cita(
                Optional.ofNullable(map.get("id")).map(AttributeValue::s).orElse("uuid"),
                Optional.ofNullable(map.get("hora")).map(AttributeValue::n).orElse("0"),
                Optional.ofNullable(map.get("agenda")).map(AttributeValue::s).orElse("BeveerlySpa"),
                Optional.ofNullable(map.get("cliente")).map(AttributeValue::s).orElse("BeveerlyCli"),
                Optional.ofNullable(map.get("servicios")).map(AttributeValue::l)
                        .map(attributeValues -> new ServicioMapper().map(attributeValues))
                        .orElse(Collections.emptyList()),
                Optional.ofNullable(map.get("telefono")).map(AttributeValue::s).orElse("+57 "),
                Optional.ofNullable(map.get("porcentaje")).map(AttributeValue::n).orElse("100")
        );
    }

    @Override
    public List<Cita> map(List<AttributeValue> list) {
        return list.stream().map(AttributeValue::m).map(this::map).collect(Collectors.toList());
    }
}
