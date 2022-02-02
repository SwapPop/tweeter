package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    //View

    public interface View {
        void displayMessage(String message);
        void setFollowing(boolean isFollower);

        void updateFollowButton(boolean removed);
        void updateSelectedUserFollowingAndFollowers();

        void enableFollowButton();

        void setFollowersCount(int count);
        void setFollowingCount(int count);

        void logoutUser();

        void showLogoutToast();
        void cancelLogoutToast();
    }

    //Services

    private MainPresenter.View view;
    private FollowService followService;
    private UserService userService;
    private StatusService statusService;

    //Constructor

    public MainPresenter(MainPresenter.View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
        statusService = new StatusService();
    }

    //"Get" Functions

    public void isFollower(User selectedUser) {
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), selectedUser, new MainPresenter.IsFollowerObserver());
    }

    public void follow(User selectedUser) {
        followService.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new MainPresenter.FollowObserver());
    }

    public void unfollow(User selectedUser) {
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new MainPresenter.UnfollowObserver());
    }

    public void getFollowersCount(User selectedUser) {
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new MainPresenter.GetFollowersCountObserver());
    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new MainPresenter.GetFollowingCountObserver());
    }

    public void logout() {
        view.showLogoutToast();
        userService.logout(Cache.getInstance().getCurrUserAuthToken(), new MainPresenter.LogoutObserver());
    }

    //Observer implementation(s)

    public class IsFollowerObserver implements FollowService.IsFollowerObserver {
        @Override
        public void handleSuccess(boolean isFollower) {
            view.setFollowing(isFollower);
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }
    }

    public class FollowObserver implements FollowService.FollowObserver {
        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(false);
            view.enableFollowButton();
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow: " + message);
            view.enableFollowButton();
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to follow because of exception: " + exception.getMessage());
            view.enableFollowButton();
        }
    }

    public class UnfollowObserver implements FollowService.UnfollowObserver {
        @Override
        public void handleSuccess() {
            view.updateSelectedUserFollowingAndFollowers();
            view.updateFollowButton(true);
            view.enableFollowButton();
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to unfollow: " + message);
            view.enableFollowButton();
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to unfollow because of exception: " + exception.getMessage());
            view.enableFollowButton();
        }
    }

    public class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver {
        @Override
        public void handleSuccess(int count) {
            view.setFollowersCount(count);
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get followers count because of exception: " + exception.getMessage());
        }
    }

    public class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver {
        @Override
        public void handleSuccess(int count) {
            view.setFollowingCount(count);
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following count because of exception: " + exception.getMessage());
        }
    }

    public class LogoutObserver implements UserService.LogoutObserver {
        @Override
        public void handleSuccess() {
            view.cancelLogoutToast();
            view.logoutUser();
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to logout because of exception: " + exception.getMessage());
        }
    }
}
