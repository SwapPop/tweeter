package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String USER_KEY = "user";


    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler, authToken);
        this.alias = alias;
    }


    private User getUser() {
        return user;
    }

    @Override
    protected void runTask() {
        user = getFakeData().findUserByAlias(alias);
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, getUser());
    }
}
