package mapper;

import acl.DynamoMapper;
import domain.Agenda;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

public class AgendaMapper implements DynamoMapper<Agenda> {

    public Agenda map(Map<String, AttributeValue> map) {
        return new Agenda(
                Optional.ofNullable(map.get("id")).map(AttributeValue::s).orElse("undefined"),
                Optional.ofNullable(map.get("name")).map(AttributeValue::s).orElse("undefined")
        );
    }
}
