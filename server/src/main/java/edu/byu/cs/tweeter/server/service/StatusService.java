package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class StatusService {

    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getStoryDAO().getStory(request);
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        return getFeedDAO().getFeed(request);
    }

    StatusDAO getStoryDAO() {
        return new StatusDAO();
    }
    StatusDAO getFeedDAO() { return new StatusDAO();}
}
