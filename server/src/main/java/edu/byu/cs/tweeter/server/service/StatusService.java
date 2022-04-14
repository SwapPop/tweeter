package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.BatchFeedRequest;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersQueueRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.BatchFeedResponse;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactoryProvider;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {

    DAOFactoryProvider daoProvider;

    public StatusService() {
        this.daoProvider = new DAOFactoryProvider();
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new StoryResponse("Session expired");
        }
        return getStatusDAO().getStory(request);
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new FeedResponse("Session expired");
        }
        return getStatusDAO().getFeed(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[BadRequest] Missing an authToken");
        } else if(request.getStatus() == null){
            throw new RuntimeException("[BadRequest] Missing a status");
        }

        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new PostStatusResponse("Session expired");
        }

        getStatusDAO().postStatusToStory(request);

        GetFollowersQueueRequest batchRequest = new GetFollowersQueueRequest(request.getStatus());

        String messageBody = JsonSerializer.serialize(batchRequest);
        SendMessageRequest SMSrequest = new SendMessageRequest().withQueueUrl("https://sqs.us-east-1.amazonaws.com/924350594575/tweeterGetFollowersQueue").withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(SMSrequest);

        return new PostStatusResponse();
    }

    public void batchFeedUpdate(BatchFeedRequest request) {
        getStatusDAO().addStatusBatchToFeed(request.getFollowersAliases(), request.getStatus());
    }

    StatusDAO getStatusDAO() {
        return daoProvider.getDaoFactory().getStatusDAO();
    }
    AuthTokenDAO getAuthTokenDAO() {
        return daoProvider.getDaoFactory().getAuthTokenDAO();
    }
}
