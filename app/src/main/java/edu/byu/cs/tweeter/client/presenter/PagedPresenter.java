package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter{

    protected static final int PAGE_SIZE = 10;
    PagedView<T> view;
    protected T lastItem;

    private UserService userService;



    public PagedPresenter(PagedView<T> view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }



    public interface PagedView<T> extends Presenter.View {
        void setLoadingStatus(boolean value);

        void addItems(List<T> items);

        void startMainActivity(User thisUser);
    }



    private boolean hasMorePages;
    private boolean isLoading = false;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingStatus(true);
        }
        getServiceItems(user);
    }

    protected abstract void getServiceItems(User user);

    public void getUser(String userAlias) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    public class PagedTemplateObserver implements PagedObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            setLoading(false);
            view.setLoadingStatus(false);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);

            view.addItems(items);
        }

        @Override
        public void handleFailure(String message) {
            setLoading(false);
            view.setLoadingStatus(false);
            view.displayMessage("Failed to " + getActionString() + ": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            setLoading(false);
            view.setLoadingStatus(false);
            view.displayMessage("Failed to " + getActionString()+ " because of exception: " + exception);
        }
    }


    public class GetUserObserver implements edu.byu.cs.tweeter.client.model.service.observer.GetUserObserver {

        @Override
        public void handleSuccess(User thisUser) {
            view.startMainActivity(thisUser);
            view.displayMessage("Getting user's profile...");
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }
    }

}
