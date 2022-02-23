package acl;

import acl.types.BeverlyHttpAuthObject;
import acl.types.BeverlyHttpReqAttrib;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class BeverlyAuthAction extends Action.Simple {

    static final int EIGHT_HOURS_IN_MILLIS = 28800000;

    public CompletionStage<Result> call(Http.Request req) {
        System.out.println(req);
        return req.queryString("access_token")
                .flatMap(accessToken -> getSessionItem(accessToken)
                        .flatMap(beverlyHttpAuthObject -> {
                            System.out.println(" with " + beverlyHttpAuthObject);
                            return System.currentTimeMillis() - beverlyHttpAuthObject.expiresAt < EIGHT_HOURS_IN_MILLIS ?
                                    Optional.of(delegate.call(req.addAttr(BeverlyHttpReqAttrib.USER, beverlyHttpAuthObject))) :
                                    Optional.empty();
                        })).orElse(delegate.call(req));
    }

    private Optional<BeverlyHttpAuthObject> getSessionItem(String httpReqAccessToken) {
        return BeverlyDynamoDB.getItem(
                "auth",
                "accessToken",
                httpReqAccessToken
        ).map(BeverlyHttpAuthObject::new);
    }
}
