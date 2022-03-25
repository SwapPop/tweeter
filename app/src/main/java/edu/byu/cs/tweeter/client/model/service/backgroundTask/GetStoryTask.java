package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends GetStatusesPagedTask {
    private static final String LOG_TAG = "GetStoryTask";
//    public static final String STATUSES_KEY = "story";

    public GetStoryTask(StatusService statusService, AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(statusService, messageHandler, authToken, targetUser, limit, lastStatus);
    }

    @Override
    protected void runTask() {

    }
}