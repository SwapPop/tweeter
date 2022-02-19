package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status>{

    protected StatusService statusService;

    public interface View extends PagedPresenter.PagedView<Status> { }

    public StoryPresenter(StoryPresenter.View view) {
        super(view);
        this.statusService = new StatusService();
    }

    @Override
    protected void getServiceItems(User user) {
        statusService.getStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new PagedTemplateObserver());
    }

    @Override
    protected String getActionString() {
        return "get story";
    }

}
