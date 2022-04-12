package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.Date;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDAODynamoDB implements AuthTokenDAO {
    private static long TIMEOUT = 3600000; //milliseconds

    @Override
    public boolean validateAuthToken(AuthToken authToken) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        Item thisToken = authTable.getItem("token", authToken.getToken());

        if (thisToken == null){
            return false;
        }
        else {
            Date now = new Date();
            long time = now.getTime();
            String timeStamp = thisToken.getString("timeStamp");
            long oldTime = Long.valueOf(timeStamp);

            if ((time - oldTime) > TIMEOUT){
                removeAuthToken(authToken);
                return false;
            }
            else {
                refreshToken(authToken, thisToken.getString("alias"));
                return true;
            }
        }
    }

    @Override
    public AuthToken addAuthToken(String alias) {
        AuthToken token = new AuthToken(UUID.randomUUID().toString(), String.valueOf(new Date().getTime()) );

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        authTable.putItem(new Item().withPrimaryKey("token", token.getToken())
                .withString("timeStamp", token.getDatetime())
                .withString("alias", alias));

        return token;
    }

    @Override
    public boolean removeAuthToken(AuthToken token) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        authTable.deleteItem("token", token.getToken());

        return true;
    }

    @Override
    public String getAliasFromToken(AuthToken authToken) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        Item token = authTable.getItem("token", authToken.getToken());
        return token.getString("alias");
    }

    @Override
    public AuthToken refreshToken(AuthToken token, String alias) {
        long date = new Date().getTime();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        authTable.putItem(new Item().withPrimaryKey("token", token.getToken())
                .withString("timeStamp", String.valueOf(date))
                .withString("alias", alias));

        return token;
    }
}
