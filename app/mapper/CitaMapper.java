package mapper;

import acl.DynamoMapper;
import domain.Cita;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

public class CitaMapper implements DynamoMapper<Cita> {

    public Cita map(Map<String, AttributeValue> map) {
        return new Cita(
                Optional.ofNullable(map.get("id")).map(AttributeValue::s).orElse("undefined"),
                Optional.ofNullable(map.get("name")).map(AttributeValue::s).orElse("undefined")
        );
    }
}
