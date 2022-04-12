package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactoryProvider;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.UserDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    DAOFactoryProvider daoProvider;

    public FollowService() {
        this.daoProvider = new DAOFactoryProvider();
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAODynamoDB} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new FollowingResponse("Session expired");
        }
        return getFollowDAO().getFollowees(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new FollowersResponse("Session expired");
        }
        return getFollowDAO().getFollowers(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a valid follower alias");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a valid followee alias");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new IsFollowerResponse("Session expired");
        }
        return getFollowDAO().isFollower(request);
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an authToken");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a valid followee alias");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new FollowResponse("Session expired");
        }
        String userAlias = getAuthTokenDAO().getAliasFromToken(request.getAuthToken());
        //TODO: Stuff like this may slow down the process quite a bit
        User follower = getUserDAO().getUserByAlias(userAlias);
        FollowResponse response = getFollowDAO().follow(request, follower);
        if (response.isSuccess()){
            getUserDAO().increaseFollowCounts(request, userAlias);
        }
        return response;
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an authToken");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a valid followee alias");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new UnfollowResponse("Session expired");
        }
        String userAlias = getAuthTokenDAO().getAliasFromToken(request.getAuthToken());
        UnfollowResponse response = getFollowDAO().unfollow(request, userAlias);
        if (response.isSuccess()){
            getUserDAO().decreaseFollowCounts(request, userAlias);
        }
        return response;
    }

    /**
     * Returns an instance of {@link FollowDAODynamoDB}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return daoProvider.getDaoFactory().getFollowDAO();
    }
    UserDAO getUserDAO() {
        return daoProvider.getDaoFactory().getUserDAO();
    }
    AuthTokenDAO getAuthTokenDAO() {
        return daoProvider.getDaoFactory().getAuthTokenDAO();
    }
}
