package acl;

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
import software.amazon.awssdk.services.sqs.model.*;
import sqs.ActionsEvent;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class BeverlySQS {

    private static SqsClient client;
    private static final String queueUrl =  "https://sqs.us-west-1.amazonaws.com/711328658350/agendas";
    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;
    private ActionsEvent actionsSubscriber;

    @Inject
    public BeverlySQS(Config config, ActorSystem actorSystem, ExecutionContext executionContext, ActionsEvent actionsSubscriber) {
        System.out.println("BeberlySQS enabled.");
        this.actionsSubscriber = actionsSubscriber;
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        client = SqsClient.builder()
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

                            List<Message> messages = client.receiveMessage(build).messages();

                            for (Message m: messages) {
                                System.out.println(m.body());
                                process(m);
                                remove(m);
                            }
                        },
                        this.executionContext);
    }

    private void process(Message m) {
        BeverlyMsg beverlyMsg = Json.fromJson(Json.parse(m.body()), BeverlyMsg.class);
        actionsSubscriber.update(beverlyMsg.getEvent(), Json.parse(m.body()).get("msg").toString());
    }

    private void remove(Message m) {
        try {
            DeleteMessageRequest request = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(m.receiptHandle()).build();
            client.deleteMessage(request);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void send(String msg) {
        try {
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(msg)
                    .build();
            client.sendMessage(request);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
