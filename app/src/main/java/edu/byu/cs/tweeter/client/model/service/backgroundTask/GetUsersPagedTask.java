package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

public abstract class GetUsersPagedTask extends PagedTask<User>{
    protected FollowService followService;

    private User lastUser;

    public GetUsersPagedTask(FollowService followService, Handler messageHandler, AuthToken authToken, User targetUser, int limit, User lastUser) {
        super(messageHandler, authToken, targetUser, limit);
        this.followService = followService;
        this.lastUser = lastUser;
    }

    public User getLastUser() {
        return lastUser;
    }
}
