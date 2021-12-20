package acl;

import acl.types.Attrs;
import acl.types.Session;
import com.google.inject.Inject;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class AuthAction extends Action.Simple {

    private DynamoDbClient ddb;
    static final int ONE_DAY_IN_MILLIS = 86400000;

    @Inject
    public AuthAction(BeverlyRepo beverlyRepository) {
        ddb = beverlyRepository.getDdb();
    }

    public CompletionStage<Result> call(Http.Request req) {
        System.out.println(req);
        Optional<String> accessToken = req.queryString("access_token");

        if (accessToken.isPresent()) {
            Optional<Session> session = getSessionItem(accessToken);

            if (session.isPresent()) {
                long lastRefresh = session.get().getLastRefresh();
                long currentMillis = System.currentTimeMillis();

                if (currentMillis - lastRefresh < ONE_DAY_IN_MILLIS) {
                    updateSessionLastRefresh(session.get(), currentMillis);
                    return delegate.call(req.addAttr(Attrs.USER, session.get().getUser()));
                }
            }
        }
        return delegate.call(req);
    }

    private Optional<Session> getSessionItem(Optional<String> accessToken) {
        return DynamoDB.getItem(
                ddb,
                "sessions",
                "accessToken",
                accessToken.get()
        ).map(Session::new);
    }

    private void updateSessionLastRefresh(Session session, long currentMillis) {
        session.setLastRefresh(currentMillis);
        DynamoDB.putItem(ddb, "sessions", session);
    }

}
