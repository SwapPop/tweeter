package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

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

    public String convertImage(Bitmap image) {
        // Convert image to byte array.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public void validateRegistration(int firstNameLength, int lastNameLength, int aliasLength, char aliasSymbol, int passwordLength, Object imageToUpload) {
        if (firstNameLength == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastNameLength == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (aliasLength== 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (aliasSymbol != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (aliasLength < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (passwordLength == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}
