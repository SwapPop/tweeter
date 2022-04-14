package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public class UserDAODynamoDB implements UserDAO{

    private static String S3_IMAGES_BUCKET = "jtay11-tweeter-images";

    public UserDAODynamoDB() {
    }

    public AuthResponse login(LoginRequest request) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Item existingUser = userTable.getItem("alias", request.getUsername()).withString("password", request.getPassword());

        if (existingUser == null) {
            return new AuthResponse("Incorrect username or password.");
        } else {
            User currentUser = new User(existingUser.getString("firstName"), existingUser.getString("lastName"), existingUser.getString("alias"), existingUser.getString("image"));

            DAOFactoryProvider provider = new DAOFactoryProvider();
            AuthTokenDAO authTokenDAO = provider.getDaoFactory().getAuthTokenDAO();
            AuthToken authToken = authTokenDAO.addAuthToken(request.getUsername());

            if (authToken == null){
                return new AuthResponse("Could not start session");
            }
            return new AuthResponse(currentUser, authToken);
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        //invalidate AuthToken
        DAOFactoryProvider provider = new DAOFactoryProvider();
        AuthTokenDAO authTokenDAO = provider.getDaoFactory().getAuthTokenDAO();
        boolean success = authTokenDAO.removeAuthToken(request.getAuthToken());
        if(success) {
            return new LogoutResponse();
        } else {
            return new LogoutResponse("Failed to logout");
        }
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
                .withInt("followersCount", 0)
                .withInt("followingCount", 0)
                .withString("image", imageUrl));

        DAOFactoryProvider provider = new DAOFactoryProvider();
        AuthTokenDAO authTokenDAO = provider.getDaoFactory().getAuthTokenDAO();
        AuthToken authToken = authTokenDAO.addAuthToken(request.getUsername());

        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageUrl);

        if (authToken == null){
            return new AuthResponse("Could not start session");
        }
        return new AuthResponse(user, authToken);

    }

    public GetUserResponse findUser(GetUserRequest request){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Item thisUser = userTable.getItem("alias", request.getAlias());

        User user = new User(thisUser.getString("firstName"), thisUser.getString("lastName"), thisUser.getString("alias"), thisUser.getString("image"));
        return new GetUserResponse(user);
    }

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param request the User whose count of how many following is desired.
     * @return said count.
     */
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        //validate AuthToken from request
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        int count = userTable.getItem("alias", request.getAlias()).getInt("followingCount");

        return new GetFollowingCountResponse(count);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        //validate AuthToken from request
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        int count = userTable.getItem("alias", request.getAlias()).getInt("followersCount");

        return new GetFollowersCountResponse(count);
    }

    public boolean availableAlias(String alias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Item existingUser = userTable.getItem("alias", alias);

        if (existingUser == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void increaseFollowCounts(FollowRequest request, String userAlias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Item followee = userTable.getItem("alias", request.getFollowee().getAlias());
        int followers = followee.getInt("followersCount");
        followers++;

        userTable.putItem(new Item().withPrimaryKey("alias", followee.getString("alias"))
                .withString("password", followee.getString("password"))
                .withString("firstName", followee.getString("firstName"))
                .withString("lastName", followee.getString("lastName"))
                .withInt("followersCount", followers)
                .withInt("followingCount", followee.getInt("followingCount"))
                .withString("image", followee.getString("image")));

        Item follower = userTable.getItem("alias", userAlias);
        int following = follower.getInt("followingCount");
        following++;

        userTable.putItem(new Item().withPrimaryKey("alias", follower.getString("alias"))
                .withString("password", follower.getString("password"))
                .withString("firstName", follower.getString("firstName"))
                .withString("lastName", follower.getString("lastName"))
                .withInt("followersCount", follower.getInt("followersCount"))
                .withInt("followingCount", following)
                .withString("image", follower.getString("image")));
    }

    @Override
    public void decreaseFollowCounts(UnfollowRequest request, String userAlias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Item followee = userTable.getItem("alias", request.getFollowee().getAlias());
        int followers = followee.getInt("followersCount");
        followers--;

        userTable.putItem(new Item().withPrimaryKey("alias", followee.getString("alias"))
                .withString("password", followee.getString("password"))
                .withString("firstName", followee.getString("firstName"))
                .withString("lastName", followee.getString("lastName"))
                .withInt("followersCount", followers)
                .withInt("followingCount", followee.getInt("followingCount"))
                .withString("image", followee.getString("image")));

        Item follower = userTable.getItem("alias", userAlias);
        int following = follower.getInt("followingCount");
        following--;

        userTable.putItem(new Item().withPrimaryKey("alias", follower.getString("alias"))
                .withString("password", follower.getString("password"))
                .withString("firstName", follower.getString("firstName"))
                .withString("lastName", follower.getString("lastName"))
                .withInt("followersCount", follower.getInt("followersCount"))
                .withInt("followingCount", following)
                .withString("image", follower.getString("image")));
    }

    @Override
    public User getUserByAlias(String userHandle) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("users");

        Item item = userTable.getItem("alias", userHandle);

        return new User(item.getString("firstName"), item.getString("lastName"), item.getString("alias"), item.getString("image"));
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


    @Override
    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems("users");

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withString("firstName", user.getFirstName())
                    .withString("lastName", user.getLastName())
                    .withString("image", user.getImageUrl());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("users");
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more Users");
        }
    }
}
