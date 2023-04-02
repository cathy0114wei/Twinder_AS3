package controller;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;

public class DBController {
    public int[] getStats(DynamoDbClient ddb, String likeTable, String dislikeTable, String partitionKeyName, String partitionKeyVal, String partitionAlias, String returnKeyName) {
        int[] res = new int[2];

        List<String> likes = getRecords(ddb, likeTable, partitionKeyName, partitionKeyVal, partitionAlias, returnKeyName);
        List<String> dislikes = getRecords(ddb, dislikeTable, partitionKeyName, partitionKeyVal, partitionAlias, returnKeyName);
        res[0] = likes == null ? 0 : likes.size();
        res[1] = dislikes == null ? 0 : dislikes.size();
        System.out.println("stats res like num: " + res[0]);
        System.out.println("stats res dislike num: " + res[1]);
        return res;
    }
    public List<String> getRecords(DynamoDbClient ddb, String tableName, String partitionKeyName, String partitionKeyVal, String partitionAlias, String returnKeyName) {
        List<String> res = new ArrayList<>();
        System.out.println("getting records...");
        QueryRequest req = generateReq(tableName, partitionKeyName, partitionKeyVal, partitionAlias);
        try {
            QueryResponse response = ddb.query(req);
            if(response.count() == 0){
                System.out.println("return an empty list");
                return res;
            }
            List<Map<String, AttributeValue>> items = response.items();
            //Since the id is exclusive, then there should be only one item.
            //TODO: write a test for it.
            String values = items.get(0).get(returnKeyName).s();
            String[] strArr = values.split(",");
            for(String s : strArr){
                System.out.println("swiper are: " + s);
                res.add(s);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return res;
    }

    private QueryRequest generateReq(String tableName, String partitionKeyName, String partitionKeyVal, String partitionAlias){

        // Set up an alias for the partition key name in case it's a reserved word.
        HashMap<String,String> attrNameAlias = new HashMap<String,String>();
        attrNameAlias.put(partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value.
        HashMap<String, AttributeValue> attrValues = new HashMap<>();

        attrValues.put(":"+partitionKeyName, AttributeValue.builder()
                .s(partitionKeyVal)
                .build());

        return QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();
    }
}
