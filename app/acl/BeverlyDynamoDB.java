package acl;

import acl.types.BeverlyAttrib;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import org.apache.commons.beanutils.PropertyUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class BeverlyDynamoDB {

    private static DynamoDbClient ddb;

    @Inject
    public BeverlyDynamoDB(Config config) {
        System.out.println("BeverlyDB enabled!");
        this.ddb = DynamoDbClient.builder()
                .region(Region.US_WEST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.getString("aws.access_key_id"),
                                        config.getString("aws.secret_access_key")
                                )
                        )
                ).build();
    }

    public static Optional<Map<String, AttributeValue>> getItem(String tableName,
                                                                String key,
                                                                String keyVal) {

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(key, AttributeValue.builder().s(keyVal).build());
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = ddb.getItem(request).item();

            if (!returnedItem.isEmpty()) {
                return Optional.of(returnedItem);
            } else {
                System.out.format("No item found with the key %s %s!\n", tableName, key);
                return Optional.empty();
            }
        } catch (DynamoDbException e) {
            System.err.format("%s %s " + e.getMessage(), tableName, key);
        }
        return Optional.empty();
    }

    public static <T> T putItem(String tableName, T record) {

        HashMap<String, AttributeValue> itemValues = getAttributeValueHashMapFromRecord(record);

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();
        try {
            ddb.putItem(request);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
        return (T) record;
    }

    private static <T> HashMap<String, AttributeValue> getAttributeValueHashMapFromRecord(T record) {

        HashMap<String, AttributeValue> itemValues = new HashMap<>();

        Arrays.stream(record.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(BeverlyAttrib.class) != null)
                .forEach(field -> itemValues.put(field.getName(), getAttributeValueFromRecord(record, field)));

        return itemValues;

    }

    private static <T> Collection<HashMap<String, AttributeValue>> getAttributeValueHashMapFromRecordList(Collection<T> records) {
        return records.stream().map(BeverlyDynamoDB::getAttributeValueHashMapFromRecord).collect(Collectors.toList());
    }

    private static <T> AttributeValue getAttributeValueFromRecord(T record, Field field) {
        AttributeValue attributeValue = null;
        try {
            String type = field.getAnnotation(BeverlyAttrib.class).type();
            switch (type) {
                case "S":
                    String valueS = (String) PropertyUtils.getNestedProperty(record, field.getName());
                    attributeValue = AttributeValue.builder().s(valueS).build();
                    break;
                case "N":
                    String valueN = (String) PropertyUtils.getNestedProperty(record, field.getName());
                    attributeValue = AttributeValue.builder().n(valueN).build();
                    break;
                case "M":
                    String valueM = (String) PropertyUtils.getNestedProperty(record, field.getName());
                    Map<String, AttributeValue> value2 = getAttributeValueHashMapFromRecord(valueM);
                    attributeValue = AttributeValue.builder().m(value2).build();
                    break;
                case "L":
                    List valueL = (List) PropertyUtils.getNestedProperty(record, field.getName());
                    Collection<HashMap<String, AttributeValue>> attributeValueHashMapFromRecordList = getAttributeValueHashMapFromRecordList(valueL);
                    List<AttributeValue> collect = attributeValueHashMapFromRecordList.stream().map(stringAttributeValueHashMap -> AttributeValue.builder().m(stringAttributeValueHashMap).build()).collect(Collectors.toList());
                    attributeValue = AttributeValue.builder().l(collect).build();
                    break;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return attributeValue;
    }

    public static List<Map<String, AttributeValue>> getAll(String tableName,
                                                           String filterExpression,
                                                           Map<String, AttributeValue> values) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression(filterExpression)
                .expressionAttributeValues(values)
                .build();

        ScanResponse scan = ddb.scan(scanRequest);

        List<Map<String, AttributeValue>> items = null;

        try {
            items = scan.items();
        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }
        return items;
    }

    public static Optional<Map<String, AttributeValue>> getFirst(String tableName,
                                                                 String filterExpression,
                                                                 Map<String, AttributeValue> values) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression(filterExpression)
                .expressionAttributeValues(values)
                .build();

        ScanResponse scan = ddb.scan(scanRequest);

        try {
            return scan.items().stream().findFirst();
        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return Optional.empty();
    }

    public static void removeItem(String tableName, String key, String keyVal) {
        try {
            Map<String, AttributeValue> keyMap = new HashMap<>();
            keyMap.put(key, AttributeValue.builder().s(keyVal).build());

            DeleteItemRequest dir = DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(keyMap)
                    .build();
            ddb.deleteItem(dir);
        } catch (DynamoDbException e) {
            System.err.format("%s %s " + e.getMessage(), tableName, key);
        }
    }
}
