package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {
    public interface View {
        void displayMessage(String message);

        void registerSuccess(User registeredUser);
        void showRegisterSuccessToast();
    }

    private RegisterPresenter.View view;
    private UserService userService;

    public RegisterPresenter(RegisterPresenter.View view) {
        this.view = view;
        userService = new UserService();
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64String) {
        userService.Register(firstName, lastName, alias, password, imageBytesBase64String, new RegisterObserver());
    }

    public class RegisterObserver implements UserService.RegisterObserver {

        @Override
        public void handleSuccess(User registeredUser, AuthToken authToken) {

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.registerSuccess(registeredUser);
            view.showRegisterSuccessToast();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to register: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to register because of exception: " + exception.getMessage());
        }
    }
}
