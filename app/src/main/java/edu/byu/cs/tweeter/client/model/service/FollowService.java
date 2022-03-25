package edu.byu.cs.tweeter.client.model.service;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.handler.CountHandler;
import edu.byu.cs.tweeter.client.model.service.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service{

    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {
        GetFollowingTask getFollowingTask = getGetFollowingTask(currUserAuthToken, user, pageSize, lastFollowee, getFollowingObserver);
        executeTask(getFollowingTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {
        GetFollowersTask getFollowersTask = getGetFollowersTask(currUserAuthToken, user, pageSize, lastFollower, getFollowersObserver);
        executeTask(getFollowersTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User currentUser, User selectedUser, IsFollowerObserver isFollowerObserver){
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken, currentUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        executeTask(isFollowerTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver followObserver){
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        executeTask(followTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver unfollowObserver){
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        executeTask(unfollowTask);

    }

    //getFollowersCount
    //ExecutorService executor = Executors.newFixedThreadPool(2); to execute both?

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, CountObserver getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new CountHandler(getFollowersCountObserver));
        executeTask(followersCountTask);
    }

    //getFollowingCount

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, CountObserver getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new CountHandler(getFollowingCountObserver));
        executeTask(followingCountTask);
    }


    @NonNull
    private GetFollowingTask getGetFollowingTask(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {
        return new GetFollowingTask(this, currUserAuthToken,
                user, pageSize, lastFollowee, new PagedHandler<User>(getFollowingObserver));
    }

    @NonNull
    private GetFollowersTask getGetFollowersTask(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {
        return new GetFollowersTask(this, currUserAuthToken,
                user, pageSize, lastFollower, new PagedHandler<User>(getFollowersObserver));
    }

}
