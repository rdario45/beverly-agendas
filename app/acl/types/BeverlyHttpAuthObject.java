package acl.types;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class BeverlyHttpAuthObject {

    @BeverlyAttrib(type = "S")
    public String accessToken;

    @BeverlyAttrib(type = "N")
    public long expiresAt;

    @BeverlyAttrib(type = "S")
    public String ipv4;

    @BeverlyAttrib(type = "S")
    public String phone;

    public BeverlyHttpAuthObject(Map<String, AttributeValue> map) {
        this.accessToken = map.get("accessToken").s(); // TODO error java.lang.NullPointerException: because hardcode name 'accessToken'
        this.expiresAt = Long.parseLong(map.get("expiresAt").n());
        this.ipv4 =  map.get("ipv4").s();
        this.phone =  map.get("phone").s();
    }

    @Override
    public String toString() {
        return "{" +
                "ipv4='" + ipv4 + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}