package acl.types;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class BeverlyHttpAuthObject {

    @BeverlyAttrib(type = "S")
    public String accessToken;

    @BeverlyAttrib(type = "N")
    public long expiresAt;

    @BeverlyAttrib(type = "S")
    public String phone;

    public BeverlyHttpAuthObject(Map<String, AttributeValue> map) {
        this.accessToken = map.get("accessToken").s();
        this.expiresAt = Long.parseLong(map.get("expiresAt").n());
        this.phone =  map.get("phone").s();
    }
}