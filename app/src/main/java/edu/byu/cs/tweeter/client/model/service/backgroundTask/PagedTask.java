package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class PagedTask<T> extends AuthenticatedTask{

    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";

    /**
     * The user whose feed/story/followers/following is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    protected User targetUser;
    /**
     * Maximum number of items to return (i.e., page size).
     */
    protected int limit;
    /**
     * The last item returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */

    protected List<T> items;
    protected boolean hasMorePages;

    public PagedTask(Handler messageHandler, AuthToken authToken,  User targetUser, int limit) {
        super(messageHandler, authToken);
        this.targetUser = targetUser;
        this.limit = limit;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    public User getTargetUser() {
        return targetUser;
    }

    public int getLimit() {
        return limit;
    }
}
