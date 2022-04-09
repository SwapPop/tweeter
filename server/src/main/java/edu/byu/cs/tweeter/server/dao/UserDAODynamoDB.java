package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDAODynamoDB implements UserDAO{

    public AuthResponse login(LoginRequest request) {
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new AuthResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        //invalidate AuthToken

        return new LogoutResponse();
    }

    public AuthResponse register(RegisterRequest request) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table userTable = dynamoDB.getTable("users");

        userTable.putItem(new Item().withPrimaryKey("alias", request.getUsername())
                .withString("password", request.getPassword())
                .withString("firstName", request.getFirstName())
                .withString("lastName", request.getLastName())
                .withString("image", request.getImage()));

        //check to see if user was properly added to table


        //create authToken
        //Date().getTime()
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), new Date().toString() );

        //TODO: put authToken into authToken table if register successful

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());

        AuthResponse response = new AuthResponse(user, authToken);
        if (!addAuthToken(response)){
            return new AuthResponse("Could not start session");
        }
        return response;

    }

    public GetUserResponse findUser(GetUserRequest request){
        User user = getThisUser(request.getAlias());
        return new GetUserResponse(user);
    }

    public boolean addAuthToken(AuthResponse response){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        //TODO: may be better to make the alias the primary key with the timeStamp as the sort key
        authTable.putItem(new Item().withPrimaryKey("token", response.getAuthToken().getToken())
                .withString("timeStamp", response.getAuthToken().getDatetime())
                .withString("alias", response.getUser().getAlias()));

        return true;
    }

    public boolean availableAlias(String alias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("Alias", "Alias");

        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("alias", alias);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("Alias = alias").withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            items = userTable.query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                if(item.getString("Alias").equals(alias)) {
                    return false;
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return true;
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    public User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    User getThisUser(String alias) {
        return getFakeData().findUserByAlias(alias);
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    public AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
