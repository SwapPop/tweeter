package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;
import edu.byu.cs.tweeter.util.Pair;
import java.io.IOException;

public abstract class AuthenticateTask extends BackgroundTask{
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    private static final String LOG_TAG = "AuthenticateTask";


    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    protected String username;
    /**
     * The user's password.
     */
    protected String password;

    protected final UserService userService;

    private User authenticatedUser;
    private AuthToken authToken;
    protected String urlPath;


    public AuthenticateTask(UserService userService, Handler messageHandler, String username, String password, String urlPath) {
        super(messageHandler);
        this.password = password;
        this.username = username;
        this.userService = userService;
        this.urlPath = urlPath;
    }

    @Override
    protected void runTask() {
        try {
            AuthResponse response = getResponse();

            if (response.isSuccess()) {
                authenticatedUser = response.getUser();
                authToken = response.getAuthToken();
                sendSuccessMessage();
            } else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    protected abstract AuthResponse getResponse() throws IOException, TweeterRemoteException;

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, authenticatedUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }
}
