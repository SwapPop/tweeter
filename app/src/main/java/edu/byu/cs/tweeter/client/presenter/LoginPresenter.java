package edu.byu.cs.tweeter.client.presenter;

import android.content.Intent;
import android.widget.Toast;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;


public class LoginPresenter {
    public interface View {
        void displayMessage(String message);

        void loginSuccess(User loggedInUser);
    }

    private LoginPresenter.View view;
    private UserService userService;

    public LoginPresenter(LoginPresenter.View view) {
        this.view = view;
        userService = new UserService();
    }

    public void login(String alias, String password) {
        userService.Login(alias, password, new GetLoginObserver());
    }

    public class GetLoginObserver implements UserService.GetLoginObserver {

        @Override
        public void handleSuccess(User loggedInUser, AuthToken authToken) {
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.loginSuccess(loggedInUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to login because of exception: " + exception.getMessage());
        }
    }
}
