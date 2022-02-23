package acl;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

public interface BeverlyDynamoMapper<T> {
    T map(Map<String, AttributeValue> map);

    List<T> map(List<AttributeValue> list);
}