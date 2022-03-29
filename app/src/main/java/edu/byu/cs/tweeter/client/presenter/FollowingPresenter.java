package edu.byu.cs.tweeter.client.presenter;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User>{

    protected FollowService followService;

    public interface View extends PagedPresenter.PagedView<User>{}

    public FollowingPresenter(View view) {
        super(view);
        this.followService = getFollowService();
    }

    @NonNull
    public FollowService getFollowService() {
        if (followService == null) {
            followService = new FollowService();
        }
        return followService;
    }

    @Override
    protected void getServiceItems(User user) {
        getFollowService().getFollowing(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new PagedTemplateObserver());
    }

    @Override
    protected String getActionString() {
        return "get following";
    }
}
