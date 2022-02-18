package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;


public class LoginPresenter extends AuthPresenter{

    public interface View extends Presenter.View{
        void loginSuccess(User loggedInUser);

        void showLoginInToast();
        void showLoginSuccessToast();
    }

    private LoginPresenter.View view;
    private UserService userService;

    public LoginPresenter(LoginPresenter.View view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public void login(String alias, String password) {
        userService.Login(alias, password, new AuthTemplateObserver());
    }

    @Override
    public void authSuccess(User loggedInUser) {
        view.showLoginInToast();
        view.loginSuccess(loggedInUser);
        view.showLoginSuccessToast();
    }

    @Override
    protected String getActionString() {
        return "log in";
    }
}
