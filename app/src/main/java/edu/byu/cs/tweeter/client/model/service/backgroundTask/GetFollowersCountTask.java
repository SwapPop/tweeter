package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends CountTask {
    private static final String LOG_TAG = "GetFollowersCountTask";

    static final String URL_PATH = "/getfollowerscount";


    public GetFollowersCountTask(FollowService followService, AuthToken authToken, User targetUser, Handler messageHandler) {
        super(followService, messageHandler, authToken, targetUser);
    }

    @Override
    protected void runTask() {
        try {
            String alias = targetUser == null ? null : targetUser.getAlias();
            GetFollowersCountRequest request = new GetFollowersCountRequest(alias, authToken);
            GetFollowersCountResponse response = followService.getServerFacade().getFollowersCount(request, URL_PATH);

            if (response.isSuccess()) {
                this.count = response.getCount();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followers count of " + targetUser.getAlias(), ex);
            sendExceptionMessage(ex);
        }
    }
}
