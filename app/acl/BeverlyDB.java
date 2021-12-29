package acl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class BeverlyDB {

    private static DynamoDbClient ddb;

    @Inject
    public BeverlyDB(Config config) {
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

    public static List<Map<String, AttributeValue>> getAll(String tableName) {
        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName).build();
        ScanResponse scan = ddb.scan(scanRequest);
        List<Map<String, AttributeValue>> items = null;
        try {
            items = scan.items();
        } catch (Exception e) {
            System.err.format("Unable to scan the table: %s", tableName);
            System.err.println(e.getMessage());
        }
        return items;
    }

    public static Optional<Map<String, AttributeValue>> getItem(String tableName,
                                                                String key,
                                                                String keyVal) {

        HashMap<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(key, AttributeValue.builder().s(keyVal).build());
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();

            if (!returnedItem.isEmpty()) {
                return Optional.of(returnedItem);
            } else {
                System.out.format("No item found with the key %s %s!\n", tableName, key);
                return Optional.empty();
            }
        } catch (DynamoDbException e) {
            System.err.format("%s %s "+e.getMessage(), tableName, key);
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
        return records.stream().map(BeverlyDB::getAttributeValueHashMapFromRecord).collect(Collectors.toList());
    }

    private static <T> AttributeValue getAttributeValueFromRecord(T record, Field field) {
        AttributeValue attributeValue = null;

        try {
            String methodName = buildMethodName(field.getName()); //TODO mejorar la dependencia creada.
            Method method = record.getClass().getDeclaredMethod(methodName);
            Object invoke = method.invoke(record);
            String value = String.valueOf(invoke);
            String type = field.getAnnotation(BeverlyAttrib.class).type();
            switch (type) {
                case "S":
                    attributeValue = AttributeValue.builder().s(value).build();
                    break;
                case "N":
                    attributeValue = AttributeValue.builder().n(value).build();
                    break;
                case "M":
                    Map<String, AttributeValue> value2 = getAttributeValueHashMapFromRecord(invoke);
                    attributeValue = AttributeValue.builder().m(value2).build();
                    break;
                case "L":
                    List invoke1 = (List) method.invoke(record);
                    Collection<HashMap<String, AttributeValue>> attributeValueHashMapFromRecordList = getAttributeValueHashMapFromRecordList(invoke1);
                    List<AttributeValue> collect = attributeValueHashMapFromRecordList.stream().map(stringAttributeValueHashMap -> AttributeValue.builder().m(stringAttributeValueHashMap).build()).collect(Collectors.toList());
                    attributeValue =  AttributeValue.builder().l(collect).build();
                    break;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return attributeValue;
    }

    private static String buildMethodName(String campo) {
        return "get"+campo.substring(0, 1).toUpperCase()+campo.substring(1);
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
        Optional<Map<String, AttributeValue>> items = null;
        try {
            items = scan.items().stream().findFirst();
        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }
        return items;
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
            System.err.format("%s %s "+e.getMessage(), tableName, key);
        }
    }
}