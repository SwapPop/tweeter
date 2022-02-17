package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service {

    public interface GetFeedObserver extends PagedObserver<Status> { }

    public interface GetStoryObserver extends PagedObserver<Status> { }

    public interface PostStatusObserver extends SimpleNotificationObserver { }


    public void getFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StatusService.GetFeedObserver getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedHandler(getFeedObserver));
        executeTask(getFeedTask);
    }

    public void getStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StatusService.GetStoryObserver getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedHandler(getStoryObserver));
        executeTask(getStoryTask);
    }

    public void postStatus(AuthToken currUserAuthToken, Status newStatus, PostStatusObserver postStatusObserver) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new PostStatusHandler(postStatusObserver));
        executeTask(statusTask);
    }

}
