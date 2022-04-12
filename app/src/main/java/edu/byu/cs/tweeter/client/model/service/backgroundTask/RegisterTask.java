package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {
    private static final String LOG_TAG = "RegisterTask";
    private static final String URL_PATH = "/register";

    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;

    public RegisterTask(UserService userService, String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(userService, messageHandler, username, password, URL_PATH);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    protected AuthResponse getResponse() throws IOException, TweeterRemoteException {
        RegisterRequest request = new RegisterRequest(username, password, firstName, lastName, image);
        AuthResponse response = userService.getServerFacade().register(request, urlPath);
        return response;
    }
}
