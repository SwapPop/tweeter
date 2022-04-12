package edu.byu.cs.tweeter.client.model.service;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {

    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getFeedObserver) {
        GetFeedTask getFeedTask = getGetFeedTask(currUserAuthToken, user, pageSize, lastStatus, getFeedObserver);
        executeTask(getFeedTask);
    }

    public void getStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getStoryObserver) {
        GetStoryTask getStoryTask = getGetStoryTask(currUserAuthToken, user, pageSize, lastStatus, getStoryObserver);
        executeTask(getStoryTask);
    }

    public void postStatus(Status newStatus, SimpleNotificationObserver postStatusObserver) {
        PostStatusTask statusTask = getPostStatusTask(newStatus, postStatusObserver);
        executeTask(statusTask);
    }

    @NonNull
    private PostStatusTask getPostStatusTask(Status newStatus, SimpleNotificationObserver postStatusObserver) {
        return new PostStatusTask(this, Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new SimpleNotificationHandler(postStatusObserver));
    }


    @NonNull
    private GetFeedTask getGetFeedTask(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getFeedObserver) {
        return new GetFeedTask(this, currUserAuthToken,
                user, pageSize, lastStatus, new PagedHandler<Status>(getFeedObserver));
    }

    @NonNull
    private GetStoryTask getGetStoryTask(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getStoryObserver) {
        return new GetStoryTask(this, currUserAuthToken,
                user, pageSize, lastStatus, new PagedHandler<Status>(getStoryObserver));
    }

}
