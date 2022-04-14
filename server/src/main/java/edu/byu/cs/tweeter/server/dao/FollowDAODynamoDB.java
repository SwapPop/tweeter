package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.BatchFeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAODynamoDB implements FollowDAO{

    public FollowResponse follow(FollowRequest request, User follower) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("follows");

        userTable.putItem(new Item().withPrimaryKey("follower_handle", follower.getAlias(), "followee_handle", request.getFollowee().getAlias())
                .withString("follower_firstName", follower.getFirstName())
                .withString("follower_lastName", follower.getLastName())
                .withString("followee_firstName", request.getFollowee().getFirstName())
                .withString("followee_lastName", request.getFollowee().getLastName()));


        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request, String userAlias) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("follows");

        userTable.deleteItem("follower_handle", userAlias,"followee_handle", request.getFollowee().getAlias());

        return new UnfollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("follows");

        Item follows = userTable.getItem("follower_handle", request.getFollower(),"followee_handle", request.getFollowee());

        boolean isFollower;
        if (follows == null){
            isFollower = false;
        } else {
            isFollower = true;
        }

        return new IsFollowerResponse(isFollower);
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        AmazonDynamoDB client=AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        DynamoDB dynamoDB=new DynamoDB(client);

        Table table=dynamoDB.getTable("follows");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":frh", request.getFollowerAlias());

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :frh")
                .withValueMap(valueMap).withScanIndexForward(false);

        ItemCollection<QueryOutcome> followees = null;
        Iterator<Item> iter = null;
        Item item = null;
        List<User> allFollowees = new ArrayList<>();

        try {
            followees = table.query(querySpec);

            iter = followees.iterator();
            while (iter.hasNext()) {
                item = iter.next();
                String followeeHandle = item.getString("followee_handle");
                DAOFactoryProvider provider = new DAOFactoryProvider();
                User user = provider.getDaoFactory().getUserDAO().getUserByAlias(followeeHandle);
                allFollowees.add(user);
            }

        } catch (Exception e) {
            System.err.println("Unable to query relationship");
            System.err.println(e.getMessage());
        }

        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;

        BatchFeedResponse response = getAllFollowersAliases(request.getFolloweeAlias(), request.getLimit(), request.getLastFollowerAlias());
        List<String> followerHandles = response.getFollowersAliases();
        boolean hasMorePages = response.getHasMorePages();

        DAOFactory factory = new DAOFactoryProvider().getDaoFactory();

        List<User> followersPage = new ArrayList<>();
        for (String handle : followerHandles) {
            User user = factory.getUserDAO().getUserByAlias(handle);
            followersPage.add(user);
        }

        return new FollowersResponse(followersPage, hasMorePages);
    }

    @Override
    public BatchFeedResponse getAllFollowersAliases(String alias, int limit, String lastFollowerAlias) {
        AmazonDynamoDB client=AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
        DynamoDB dynamoDB=new DynamoDB(client);
        Table table=dynamoDB.getTable("follows");
        Index index =table.getIndex("follows_index");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":feh", alias);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("followee_handle = :feh")
                .withValueMap(valueMap).withScanIndexForward(true);

        ItemCollection<QueryOutcome> followers;
        Iterator<Item> iter;
        Item item;
        List<String> followerHandles = new ArrayList<>();

        List<String> followerPage = new ArrayList<>();

        boolean hasMorePages = false;

        try {
            followers = index.query(querySpec);

            iter = followers.iterator();
            while (iter.hasNext()) {
                item = iter.next();
                String followerHandle = item.getString("follower_handle");
                followerHandles.add(followerHandle);
            }

        } catch (Exception e) {
            System.err.println("Unable to query relationship");
            System.err.println(e.getMessage());
        }

        if(limit > 0) {
            if (followerHandles != null) {
                int followersIndex = getFollowersStartingIndex(lastFollowerAlias, followerHandles);

                for(int limitCounter = 0; followersIndex < followerHandles.size() && limitCounter < limit; followersIndex++, limitCounter++) {
                    followerPage.add(followerHandles.get(followersIndex));
                }
                hasMorePages = followersIndex < followerHandles.size();
            }
        }

        return new BatchFeedResponse(followerPage, hasMorePages);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    private int getFollowersStartingIndex(String lastFollowerAlias, List<String> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }
}
