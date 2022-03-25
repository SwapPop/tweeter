package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends GetUsersPagedTask {
    private static final String LOG_TAG = "GetFollowingTask";
//    public static final String FOLLOWEES_KEY = "following";

    static final String URL_PATH = "/getfollowing";

    public GetFollowingTask(FollowService followService, AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(followService, messageHandler, authToken, targetUser, limit, lastFollowee);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            //TODO: NOT SURE THIS WILL WORK
            String lastFolloweeAlias = getLastUser() == null ? null : getLastUser().getAlias() ;

            FollowingRequest request = new FollowingRequest(authToken, targetUserAlias, limit, lastFolloweeAlias);
            FollowingResponse response = followService.getServerFacade().getFollowees(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getFollowees();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get followees", ex);
            sendExceptionMessage(ex);
        }
    }
}
