package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class GetStatusesPagedTask extends PagedTask<Status>{
    protected StatusService statusService;

    private Status lastStatus;

    public GetStatusesPagedTask(StatusService statusService, Handler messageHandler, AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        super(messageHandler, authToken, targetUser, limit);
        this.statusService = statusService;
        this.lastStatus = lastStatus;
    }

    public Status getLastStatus() {
        return lastStatus;
    }
}
