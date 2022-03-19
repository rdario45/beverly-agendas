package acl;

import acl.types.BeverlyEvent;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import play.libs.Json;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import sqs.BeverlyActionsEvent;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class BeverlySQS {

    private static SqsClient sqsClient;
    private static String queueUrl;
    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;
    private BeverlyActionsEvent beverlyActionsEvent;

    @Inject
    public BeverlySQS(Config config, ActorSystem actorSystem, ExecutionContext executionContext, BeverlyActionsEvent actionsSubscriber) {
        this.queueUrl = config.getString("aws.queueUrl");
        this.beverlyActionsEvent = actionsSubscriber;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        sqsClient = SqsClient.builder()
                .region(Region.US_WEST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.getString("aws.access_key_id"),
                                        config.getString("aws.secret_access_key")
                                )
                        )
                ).build();
        this.initialize();
    }

    private void initialize() {
        this.actorSystem
                .scheduler()
                .scheduleAtFixedRate(
                        Duration.create(10, TimeUnit.SECONDS), // initialDelay
                        Duration.create(10, TimeUnit.SECONDS), // interval
                        () -> {
                            ReceiveMessageRequest build = ReceiveMessageRequest
                                    .builder()
                                    .queueUrl(queueUrl)
                                    .build();

                            List<Message> messages = sqsClient.receiveMessage(build).messages();

                            for (Message message : messages) {
                                process(message);
                                remove(message);
                            }
                        },
                        this.executionContext);
    }

    private void process(Message m) {
        BeverlyEvent beverlyEvent = Json.fromJson(Json.parse(m.body()), BeverlyEvent.class);
        beverlyActionsEvent.update(beverlyEvent.getMyClass(), Json.parse(m.body()).get("message").toString());
    }

    private void remove(Message m) {
        try {
            DeleteMessageRequest request = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(m.receiptHandle()).build();
            sqsClient.deleteMessage(request);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
