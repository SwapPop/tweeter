package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter {

    //View

    public interface View {
        void displayMessage(String message);
        void setFollowing(boolean isFollower);
//
//        void addFollowers(List<User> followers);
//
//        void startMainActivity(User thisUser);
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


}
