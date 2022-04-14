package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class GetFollowersQueueRequest {

    private Status status;
    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private GetFollowersQueueRequest() {}

    public GetFollowersQueueRequest(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
