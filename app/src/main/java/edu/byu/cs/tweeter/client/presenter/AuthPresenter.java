package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.AuthObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthPresenter extends Presenter{

    public AuthPresenter(View view) {
        super(view);
    }

    public abstract void authSuccess(User registeredUser);

    public class AuthTemplateObserver implements AuthObserver {

        @Override
        public void handleSuccess(User registeredUser, AuthToken authToken) {

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            authSuccess(registeredUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to "+ getActionString() +": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to "+ getActionString() +" because of exception: " + exception.getMessage());
        }
    }

}
