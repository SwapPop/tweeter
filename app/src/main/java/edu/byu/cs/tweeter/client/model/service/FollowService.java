package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service{

    public interface GetFollowingObserver<T> extends PagedObserver<T> { }

    public interface GetFollowersObserver<T> extends PagedObserver<T> { }

    public interface IsFollowerObserver extends ServiceObserver {
        void handleSuccess(boolean isFollower);
    }

    public interface FollowObserver extends SimpleNotificationObserver {}

    public interface UnfollowObserver extends SimpleNotificationObserver {}

    public interface GetFollowersCountObserver extends CountObserver { }

    public interface GetFollowingCountObserver extends CountObserver{ }


    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, GetFollowingObserver getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedHandler(getFollowingObserver));
        executeTask(getFollowingTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, GetFollowersObserver getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedHandler(getFollowersObserver));
        executeTask(getFollowersTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User currentUser, User selectedUser, IsFollowerObserver isFollowerObserver){
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken, currentUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        executeTask(isFollowerTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, FollowObserver followObserver){
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        executeTask(followTask);

    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, UnfollowObserver unfollowObserver){
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        executeTask(unfollowTask);

    }

    //getFollowersCount
    //ExecutorService executor = Executors.newFixedThreadPool(2); to execute both?

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, GetFollowersCountObserver getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowersCountHandler(getFollowersCountObserver));
        executeTask(followersCountTask);
    }

    //getFollowingCount

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, GetFollowingCountObserver getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowingCountHandler(getFollowingCountObserver));
        executeTask(followingCountTask);
    }

}
