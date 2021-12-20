package acl.types;

import acl.BeverlyAttrib;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

public class Session {

    @BeverlyAttrib(type="S")
    private String accessToken;

    @BeverlyAttrib(type="N")
    private long lastRefresh;

    @BeverlyAttrib(type = "M")
    private User user;
    
    public Session(Map<String, AttributeValue> map) {
        this.accessToken = map.get("accessToken").s();
        this.lastRefresh = Optional.ofNullable(map.get("lastRefresh")).map(AttributeValue::n).map(Long::parseLong).orElse(0l);
        Map<String, AttributeValue> user = Optional.ofNullable( map.get("user")).map(AttributeValue::m).get();
        this.user = new User(
            Optional.ofNullable(user.get("name")).map(AttributeValue::s).orElse("undefined"),
            Optional.ofNullable(user.get("phone")).map(AttributeValue::s).orElse("undefined")
        );
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}