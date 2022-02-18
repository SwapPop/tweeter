package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.AuthObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends AuthPresenter{


    public interface View extends Presenter.View{
        void registerSuccess(User registeredUser);
        void showRegisterSuccessToast();
    }

    private RegisterPresenter.View view;
    private UserService userService;

    public RegisterPresenter(RegisterPresenter.View view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64String) {
        userService.Register(firstName, lastName, alias, password, imageBytesBase64String, new AuthTemplateObserver());
    }
    @Override
    public void authSuccess(User registeredUser) {
        view.registerSuccess(registeredUser);
        view.showRegisterSuccessToast();
    }

    @Override
    protected String getActionString() {
        return "register";
    }
}
