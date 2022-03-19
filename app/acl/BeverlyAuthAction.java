package acl;

import acl.types.BeverlyHttpAuthObject;
import acl.types.BeverlyHttpReqAttrib;
import mapper.AgendaMapper;
import org.joda.time.DateTime;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class BeverlyAuthAction extends Action.Simple {

    static final int EIGHT_HOURS_IN_MILLIS = 28800000;

    public CompletionStage<Result> call(Http.Request req) {
        return req.queryString("access_token")
                .flatMap(accessToken -> getSessionItem(accessToken)
                        .flatMap(beverlyHttpAuthObject -> {
                            return System.currentTimeMillis() - beverlyHttpAuthObject.expiresAt < EIGHT_HOURS_IN_MILLIS ?
                                    Optional.of(delegate.call(req.addAttr(BeverlyHttpReqAttrib.USER, beverlyHttpAuthObject))) :
                                    Optional.empty();
                        })).orElse(delegate.call(req));
    }

    private Optional<BeverlyHttpAuthObject> getSessionItem(String httpReqAccessToken) {
        HashMap<String, AttributeValue> values = new HashMap<>();
        values.put(":accessToken", AttributeValue.builder().s(httpReqAccessToken).build());
        values.put(":expiresAt", AttributeValue.builder().n(""+new DateTime().getMillis()).build());
        return BeverlyDynamoDB.getFirst("auth", "accessToken = :accessToken AND expiresAt > :expiresAt", values)
                .map(BeverlyHttpAuthObject::new);
    }
}
