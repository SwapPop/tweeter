package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class CountTask extends AuthenticatedTask{
    public static final String COUNT_KEY = "count";

    private int count;

    /**
     * The user whose follower/following count is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser;

    public CountTask(Handler messageHandler, AuthToken authToken, User targetUser) {
        super(messageHandler, authToken);
        this.targetUser = targetUser;
    }

    public int getCount() {
        return count;
    }

    @Override
    protected void runTask() {
        count = 20;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        //Hardcoded fake value
        msgBundle.putInt(COUNT_KEY, getCount());
    }
}
