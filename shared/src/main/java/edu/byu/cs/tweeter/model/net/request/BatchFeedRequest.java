package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class BatchFeedRequest {

    private Status status;
    private List<String> followersAliases;
    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private BatchFeedRequest() {}

    public BatchFeedRequest(Status status, List<String> followersAliases) {
        this.status = status;
        this.followersAliases = followersAliases;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getFollowersAliases() {
        return followersAliases;
    }

    public void setFollowersAliases(List<String> followersAliases) {
        this.followersAliases = followersAliases;
    }
}
