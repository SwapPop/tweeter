package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {
    private static final String LOG_TAG = "LoginTask";
    private static final String URL_PATH = "/login";

    public LoginTask(UserService userService, String username, String password, Handler messageHandler) {
        super(userService, messageHandler, username, password, URL_PATH);
    }


    @Override
    protected AuthResponse getResponse() throws IOException, TweeterRemoteException {
        LoginRequest request = new LoginRequest(username, password);
        AuthResponse response = userService.getServerFacade().login(request, urlPath);
        return response;
    }
}
