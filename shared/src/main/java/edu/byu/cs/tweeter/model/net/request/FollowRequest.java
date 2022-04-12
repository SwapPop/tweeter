package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {

    private AuthToken authToken;
    private User followee;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowRequest() {}

    public FollowRequest(AuthToken authToken, User followee) {
        this.authToken = authToken;
        this.followee = followee;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }
}
