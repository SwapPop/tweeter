package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User>{

    protected FollowService followService;

    public interface View extends PagedPresenter.PagedView<User>{}

    public FollowersPresenter(View view) {
        super(view);
        this.followService = new FollowService();
    }

    @Override
    protected void getServiceItems(User user) {
        followService.getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new PagedTemplateObserver());
    }

    @Override
    protected String getActionString() {
        return "get followers";
    }
}
