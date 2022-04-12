package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "UnfollowTask";


    /**
     * Logged-in user is the "follower" in the relationship.
     */
    /**
     * The user that is being followed.
     */
    private User followee;

    private FollowService followService;

    static final String URL_PATH = "/unfollow";


    public UnfollowTask(FollowService followService, AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.followee = followee;
        this.followService = followService;
    }

    @Override
    protected void runTask() {
        try {
            UnfollowRequest request = new UnfollowRequest(authToken, followee);
            UnfollowResponse response = followService.getServerFacade().unfollow(request, URL_PATH);

            if (response.isSuccess()) {
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

    }
}
