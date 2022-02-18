package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.AuthObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;


public class LoginPresenter {
    public interface View {
        void displayMessage(String message);

        void loginSuccess(User loggedInUser);

        void showLoginInToast();
        void showLoginSuccessToast();
    }

    private LoginPresenter.View view;
    private UserService userService;

    public LoginPresenter(LoginPresenter.View view) {
        this.view = view;
        userService = new UserService();
    }

    public void login(String alias, String password) {
        userService.Login(alias, password, new LoginObserver());
    }

    public class LoginObserver implements AuthObserver {

        @Override
        public void handleSuccess(User loggedInUser, AuthToken authToken) {
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.showLoginInToast();
            view.loginSuccess(loggedInUser);
            view.showLoginSuccessToast();
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
