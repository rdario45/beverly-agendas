package acl;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Singleton;

@Singleton
public class BeverlyRepo {

    private DynamoDbClient ddb;

    @Inject
    public BeverlyRepo(Config config) {
        System.out.println("BeberlyRepository enabled.");
        this.ddb = DynamoDbClient.builder()
                .region(Region.US_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        System.getenv("AWS_ACCESS_KEY_ID"),
                                        System.getenv("AWS_SECRET_ACCESS_KEY")
                                )
                        )
                ).build();
    }

    public DynamoDbClient getDdb() {
        return this.ddb;
    };
}
