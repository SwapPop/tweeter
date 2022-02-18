package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.Status;
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

        void showPostToast();
        void cancelPostToast();
    }

    //Services

    private MainPresenter.View view;
    private FollowService followService;
    private UserService userService;
    private StatusService statusService;

    private static final String LOG_TAG = "MainActivity";

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

    public void post(String post) {
        view.showPostToast();
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            statusService.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new MainPresenter.PostStatusObserver());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    //Observer implementation(s)

    public class IsFollowerObserver implements edu.byu.cs.tweeter.client.model.service.observer.IsFollowerObserver {
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

    public class FollowObserver implements SimpleNotificationObserver {
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

    public class UnfollowObserver implements SimpleNotificationObserver {
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

    public class GetFollowersCountObserver implements CountObserver {
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

    public class GetFollowingCountObserver implements CountObserver {
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

    public class LogoutObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            view.cancelLogoutToast();
            view.logoutUser();
            Cache.getInstance().clearCache();
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

    public class PostStatusObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            view.cancelPostToast();
            view.displayMessage("Successfully Posted!");
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post status: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to post the status because of exception: " + exception.getMessage());
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
}



