package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followers for a specified follower.
 */
public class FeedRequest {

    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private String lastStatusPost;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FeedRequest() {}

    public FeedRequest(AuthToken authToken, String followerAlias, int limit, String lastStatusPost) {
        this.authToken = authToken;
        this.userAlias = followerAlias;
        this.limit = limit;
        this.lastStatusPost = lastStatusPost;
    }

    /**
     * Returns the auth token of the user who is making the request.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token.
     *
     * @param authToken the auth token.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }


    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    /**
     * Returns the number representing the maximum number of followees to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the limit.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastStatusPost() {
        return lastStatusPost;
    }

    public void setLastStatusPost(String lastStatusPost) {
        this.lastStatusPost = lastStatusPost;
    }
}
