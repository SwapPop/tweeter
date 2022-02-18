package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AuthenticateTask extends BackgroundTask{
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;
    /**
     * The user's password.
     */
    private String password;

    private User authenticatedUser;
    private AuthToken authToken;


    public AuthenticateTask(Handler messageHandler, String username, String password) {
        super(messageHandler);
        this.password = password;
        this.username = username;
    }

    private Pair<User, AuthToken> doAuthenticate() {
        authenticatedUser = getFakeData().getFirstUser();
        authToken = getFakeData().getAuthToken();
        return new Pair<>(authenticatedUser, authToken);
    }

    @Override
    protected void processTask() {
        Pair<User, AuthToken> authResult = doAuthenticate();

        authenticatedUser = authResult.getFirst();
        authToken = authResult.getSecond();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, authenticatedUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }
}
