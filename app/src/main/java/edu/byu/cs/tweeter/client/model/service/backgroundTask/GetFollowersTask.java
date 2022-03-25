package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends GetUsersPagedTask {
    static final String URL_PATH = "/getfollowers";

    private static final String LOG_TAG = "GetFollowersTask";
//    public static final String FOLLOWERS_KEY = "followers";
    public GetFollowersTask(FollowService followService, AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(followService, messageHandler, authToken, targetUser, limit, lastFollower);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            String lastFollowerAlias = getLastUser() == null ? null : getLastUser().getAlias();

            FollowersRequest request = new FollowersRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
            FollowersResponse response = followService.getServerFacade().getFollowers(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getFollowers();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followers", ex);
            sendExceptionMessage(ex);
        }
    }
}
