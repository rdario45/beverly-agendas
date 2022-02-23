package mapper;

import acl.BeverlyDynamoMapper;
import domain.Servicio;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServicioMapper implements BeverlyDynamoMapper<Servicio> {

    public Servicio map(Map<String, AttributeValue> map) {
        return new Servicio(
                Optional.ofNullable(map.get("nombre")).map(AttributeValue::s).orElse("undefined"),
                Optional.ofNullable(map.get("valor")).map(AttributeValue::n).orElse("0")
        );
    }

    @Override
    public List<Servicio> map(List<AttributeValue> list) {
        return list.stream().map(AttributeValue::m).map(this::map).collect(Collectors.toList());
    }
}
