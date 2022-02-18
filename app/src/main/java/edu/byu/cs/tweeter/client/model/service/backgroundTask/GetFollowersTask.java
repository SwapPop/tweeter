package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends GetUsersPagedTask {
    private static final String LOG_TAG = "GetFollowersTask";
//    public static final String FOLLOWERS_KEY = "followers";
    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastFollower);
    }
}
