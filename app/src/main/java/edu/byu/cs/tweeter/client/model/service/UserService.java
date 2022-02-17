package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.service.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.model.service.observer.AuthObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service{

    public interface GetUserObserver extends ServiceObserver {
        void handleSuccess(User thisUser);
    }

    public interface LoginObserver extends AuthObserver { }

    public interface RegisterObserver extends AuthObserver { }

    public interface LogoutObserver extends SimpleNotificationObserver { }

    public void getUser(AuthToken currUserAuthToken, String userAlias, GetUserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken, userAlias, new GetUserHandler(getUserObserver));
        executeTask(getUserTask);
    }

    public void Login(String alias, String password, LoginObserver loginObserver) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(loginObserver));
        executeTask(loginTask);
    }

    public void Register(String firstName, String lastName, String alias, String password, String imageBytesBase64, RegisterObserver registerObserver) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new RegisterHandler(registerObserver));
        executeTask(registerTask);
    }

    public void logout(AuthToken currUserAuthToken, LogoutObserver logoutObserver){
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(logoutObserver));
        executeTask(logoutTask);
    }
}
