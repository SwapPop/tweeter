package edu.byu.cs.tweeter.client.model.service;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handler.AuthHandler;
import edu.byu.cs.tweeter.client.model.service.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.AuthObserver;
import edu.byu.cs.tweeter.client.model.service.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService extends Service{

    public UserService() {
    }

    public void getUser(AuthToken currUserAuthToken, String userAlias, GetUserObserver getUserObserver) {
        GetUserTask getUserTask = getGetUserTask(currUserAuthToken, userAlias, getUserObserver);
        executeTask(getUserTask);
    }

    public void Login(String alias, String password, AuthObserver loginObserver) {
        LoginTask loginTask = getLoginTask(alias, password, loginObserver);
        executeTask(loginTask);
    }

    public void Register(String firstName, String lastName, String alias, String password, String imageBytesBase64, AuthObserver registerObserver) {
        RegisterTask registerTask = getRegisterTask(firstName, lastName, alias, password, imageBytesBase64, registerObserver);
        executeTask(registerTask);
    }

    public void logout(SimpleNotificationObserver logoutObserver){
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new SimpleNotificationHandler(logoutObserver));
        executeTask(logoutTask);
    }

    /**
     * Returns an instance of {@link LoginTask}. Allows mocking of the LoginTask class for
     * testing purposes. All usages of LoginTask should get their instance from this method to
     * allow for proper mocking.
     *
     * @return the instance.
     */
    LoginTask getLoginTask(String username, String password, AuthObserver observer) {
        return new LoginTask(this, username, password, new AuthHandler(observer));
    }

    /**
     * Returns an instance of {@link RegisterTask}. Allows for mocking.
     *
     * @return the instance.
     */
    RegisterTask getRegisterTask(String firstName, String lastName, String username, String password, String imageBytesBase64, AuthObserver observer) {
        return new RegisterTask(this, firstName, lastName, username, password, imageBytesBase64, new AuthHandler(observer));
    }

    @NonNull
    private GetUserTask getGetUserTask(AuthToken currUserAuthToken, String userAlias, GetUserObserver getUserObserver) {
        return new GetUserTask(this, currUserAuthToken, userAlias, new GetUserHandler(getUserObserver));
    }
}
