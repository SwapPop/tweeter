package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class AuthenticatedTask extends BackgroundTask{
    public AuthenticatedTask(Handler messageHandler, AuthToken authToken) {
        super(messageHandler);
        this.authToken = authToken;
    }

    /**
     * Auth token for logged-in user.
     */
    protected AuthToken authToken;

}
