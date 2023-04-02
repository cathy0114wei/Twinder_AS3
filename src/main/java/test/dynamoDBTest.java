package test;

import controller.DBController;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static constants.Constants.*;

public class dynamoDBTest {
    public static void main(String[] args) {
        DynamoDbClient dynamoDbClient;
        DBController dbController;
        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretAccessKey", secretAccessKey);
        System.setProperty("aws.sessionToken", sessionToken);
        Region region = Region.US_EAST_1;
        dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(SystemPropertyCredentialsProvider.create())
                .region(region)
                .build();
        dbController = new DBController();
    }
}
