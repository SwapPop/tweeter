package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    protected StatusService statusService;

    public interface View extends PagedPresenter.PagedView<Status>{}

    public FeedPresenter(View view) {
        super(view);
        this.statusService = new StatusService();
    }

    @Override
    protected void getServiceItems(User user) {
        statusService.getFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new PagedTemplateObserver());
    }

    @Override
    protected String getActionString() {
        return "get feed";
    }

}
