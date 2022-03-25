package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends GetStatusesPagedTask {
    private static final String LOG_TAG = "GetFeedTask";
//    public static final String STATUSES_KEY = "feed";

    static final String URL_PATH = "/feed";


    public GetFeedTask(StatusService statusService, AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(statusService, messageHandler, authToken, targetUser, limit, lastStatus);
    }

    @Override
    protected void runTask() {
        try {
            String targetUserAlias = targetUser == null ? null : targetUser.getAlias();
            //TODO: Not sure how else to identify the last post in the story
            String lastStatusPost = getLastStatus() == null ? null : getLastStatus().getPost();

            FeedRequest request = new FeedRequest(authToken, targetUserAlias, limit, lastStatusPost);
            FeedResponse response = statusService.getServerFacade().getFeed(request, URL_PATH);

            if (response.isSuccess()) {
                this.items = response.getFeed();
                this.hasMorePages = response.getHasMorePages();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get feed", ex);
            sendExceptionMessage(ex);
        }
    }
}
