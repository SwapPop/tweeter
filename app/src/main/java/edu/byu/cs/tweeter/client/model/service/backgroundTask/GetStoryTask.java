package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends GetStatusesPagedTask {
    private static final String LOG_TAG = "GetStoryTask";
//    public static final String STATUSES_KEY = "story";

    static final String URL_PATH = "/story";

    public GetStoryTask(StatusService statusService, AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(statusService, messageHandler, authToken, targetUser, limit, lastStatus);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            //TODO: Not sure how else to identify the last post
            String lastStatusPost = getLastStatus() == null ? null : getLastStatus().getPost();

            StoryRequest request = new StoryRequest(authToken, targetUserAlias, limit, lastStatusPost);
            StoryResponse response = statusService.getServerFacade().getStory(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getStory();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get story", ex);
            sendExceptionMessage(ex);
        }
    }
}