package acl;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface DynamoMapper<T> {
    T map(Map<String, AttributeValue> map);
}