package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import edu.byu.cs.tweeter.model.net.response.AuthResponse;

public class AuthTokenDAO {
    public boolean addToken(AuthResponse response){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        //TODO: may be better to make the alias the primary key with the timeStamp as the sort key
        authTable.putItem(new Item().withPrimaryKey("token", response.getAuthToken().getToken())
                .withString("timeStamp", response.getAuthToken().getDatetime())
                .withString("alias", response.getUser().getAlias()));

        return true;
    }
}
