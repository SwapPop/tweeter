package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends GetStatusesPagedTask {
    private static final String LOG_TAG = "GetFeedTask";
//    public static final String STATUSES_KEY = "feed";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastStatus);
    }
}
