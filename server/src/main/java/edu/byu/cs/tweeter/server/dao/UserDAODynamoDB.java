package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

    private static String S3_IMAGES_BUCKET = "jtay11-tweeter-images";

    public UserDAODynamoDB() {
    }

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

        String imageUrl = uploadImage(request.getImage(), request.getUsername());

        userTable.putItem(new Item().withPrimaryKey("alias", request.getUsername())
                .withString("password", request.getPassword())
                .withString("firstName", request.getFirstName())
                .withString("lastName", request.getLastName())
                .withString("image", imageUrl));

        //create authToken
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), new Date().toString() );

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageUrl);

        if (!addAuthToken(authToken, user)){
            return new AuthResponse("Could not start session");
        }
        return new AuthResponse(user, authToken);

    }

    public GetUserResponse findUser(GetUserRequest request){
        User user = getThisUser(request.getAlias());
        return new GetUserResponse(user);
    }

    public boolean addAuthToken(AuthToken token, User user){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table authTable = dynamoDB.getTable("authTokens");

        authTable.putItem(new Item().withPrimaryKey("token", token.getToken())
                .withString("timeStamp", token.getDatetime())
                .withString("alias", user.getAlias()));

        return true;
    }

    public boolean availableAlias(String alias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table userTable = dynamoDB.getTable("users");

        Item existingUser = userTable.getItem("alias", alias);

        if (existingUser == null) {
            return true;
        }
        else {
            return false;
        }

//        Map<String, String> nameMap = new HashMap<String, String>();
//        nameMap.put("alias", "alias");
//
//        Map<String, Object> valueMap = new HashMap<String, Object>();
//        valueMap.put("alias", alias);
//
//        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("alias = alias").withNameMap(nameMap)
//                .withValueMap(valueMap);
//
//        ItemCollection<QueryOutcome> items = null;
//        Iterator<Item> iterator = null;
//        Item item = null;
//
//        try {
//            if(userTable.getDescription() == null){
//                return true;
//            }
//            items = userTable.query(querySpec);
//            iterator = items.iterator();
//            while (iterator.hasNext()) {
//                item = iterator.next();
//                if (item.getString("alias").equals(alias)) {
//                    return false;
//                }
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//        return true;
    }

    public String uploadImage(String base64EncodedImage, String key) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        byte[] bytes = Base64.getDecoder().decode(base64EncodedImage);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        PutObjectRequest request = new PutObjectRequest(S3_IMAGES_BUCKET, key, stream, new ObjectMetadata())
                .withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(request);
        return s3.getUrl(S3_IMAGES_BUCKET, key).toString();
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
