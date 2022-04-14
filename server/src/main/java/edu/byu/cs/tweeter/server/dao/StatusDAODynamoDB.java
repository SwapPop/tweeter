package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StatusDAODynamoDB implements StatusDAO{

    public PostStatusResponse postStatus(PostStatusRequest request, List<User> followers) {
        AmazonDynamoDB client= AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
        DynamoDB dynamoDB=new DynamoDB(client);
        Table storyTable=dynamoDB.getTable("story");
        Table feedTable=dynamoDB.getTable("feed");

        String senderAlias = request.getStatus().getUser().getAlias();
        String date = request.getStatus().getDate();
        String post = request.getStatus().getPost();
        List<String> mentions = request.getStatus().getMentions();
        List<String> urls = request.getStatus().getUrls();

        storyTable.putItem(new Item().withPrimaryKey("sender_alias", senderAlias, "date_time", date)
                .withString("post", post)
                .withList("mentions", mentions)
                .withList("urls", urls));

        for(User f : followers){
            feedTable.putItem(
                    new Item().withPrimaryKey("receiver_alias", f.getAlias(), "date_time", date)
                            .withString("post", post)
                            .withString("sender_alias", senderAlias)
                            .withList("mentions", mentions)
                            .withList("urls", urls)
            );
        }


        return new PostStatusResponse("Successfully Posted!");
    }

    public StoryResponse getStory(StoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

//        List<Status> allStatuses = getDummyStatuses();

        AmazonDynamoDB client=AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
        DynamoDB dynamoDB=new DynamoDB(client);
        Table table=dynamoDB.getTable("story");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":sal", request.getUserAlias());

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("sender_alias = :sal")
                .withValueMap(valueMap).withScanIndexForward(false);

        ItemCollection<QueryOutcome> story = null;
        Iterator<Item> iter = null;
        Item item = null;
        List<Status> allStatuses = new ArrayList<>();

        try {
            story = table.query(querySpec);

            iter = story.iterator();
            while (iter.hasNext()) {
                item = iter.next();
                String senderAlias = item.getString("sender_alias");
                String dateTime = item.getString("date_time");
                String post = item.getString("post");
                List<String> mentions = item.getList("mentions");
                List<String> urls = item.getList("urls");
                DAOFactoryProvider provider = new DAOFactoryProvider();
                User user = provider.getDaoFactory().getUserDAO().getUserByAlias(senderAlias);
                Status status = new Status(post, user, dateTime, urls, mentions);
                allStatuses.add(status);
            }

        } catch (Exception e) {
            System.err.println("Unable to query relationship");
            System.err.println(e.getMessage());
        }


        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusIndex = getStatusesStartingIndex(request.getLastStatusPost(), allStatuses);

                for(int limitCounter = 0; statusIndex < allStatuses.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatuses.size();
            }
        }

        return new StoryResponse(responseStatuses, hasMorePages);
    }

    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        AmazonDynamoDB client=AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
        DynamoDB dynamoDB=new DynamoDB(client);
        Table table=dynamoDB.getTable("feed");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":ral", request.getUserAlias());

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("receiver_alias = :ral")
                .withValueMap(valueMap).withScanIndexForward(false);

        ItemCollection<QueryOutcome> feed = null;
        Iterator<Item> iter = null;
        Item item = null;
        List<Status> allStatuses = new ArrayList<>();

        try {
            feed = table.query(querySpec);

            iter = feed.iterator();
            while (iter.hasNext()) {
                item = iter.next();
                String senderAlias = item.getString("sender_alias");
                String dateTime = item.getString("date_time");
                String post = item.getString("post");
                List<String> mentions = item.getList("mentions");
                List<String> urls = item.getList("urls");
                DAOFactoryProvider provider = new DAOFactoryProvider();
                User user = provider.getDaoFactory().getUserDAO().getUserByAlias(senderAlias);
                Status status = new Status(post, user, dateTime, urls, mentions);
                allStatuses.add(status);
            }

        } catch (Exception e) {
            System.err.println("Unable to query relationship");
            System.err.println(e.getMessage());
        }
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatuses != null) {
                int statusIndex = getStatusesStartingIndex(request.getLastStatusPost(), allStatuses);

                for(int limitCounter = 0; statusIndex < allStatuses.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatuses.size();
            }
        }

        return new FeedResponse(responseStatuses, hasMorePages);
    }

    private int getStatusesStartingIndex(String lastStatusPost, List<Status> allStatuses) {

        int statusIndex = 0;

        if(lastStatusPost != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatusPost.equals(allStatuses.get(i).getPost())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                    break;
                }
            }
        }

        return statusIndex;
    }

    /**
     * Returns the list of dummy status data. This is written as a separate method to allow
     * mocking of the statuses.
     *
     * @return the statuses.
     */
    List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }


    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
