package acl;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import javax.inject.Singleton;

@Singleton
public class BeverlySNS {

    private static SnsClient snsClient;
    private static String topicArn;

    @Inject
    public BeverlySNS(Config config) {
        this.topicArn = config.getString("aws.topicArn");
        snsClient = SnsClient.builder().region(Region.US_WEST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        config.getString("aws.access_key_id"),
                                        config.getString("aws.secret_access_key")
                                )
                        )
                ).build();
    }

    public static void send(String message) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();
            snsClient.publish(request);
            System.out.println(request);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
