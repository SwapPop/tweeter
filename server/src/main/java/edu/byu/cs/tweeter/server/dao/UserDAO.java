package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface UserDAO {
    AuthResponse login(LoginRequest request);

    LogoutResponse logout(LogoutRequest request);

    AuthResponse register(RegisterRequest request);

    GetUserResponse findUser(GetUserRequest request);

    boolean availableAlias(String username);

    boolean addAuthToken(AuthResponse response);
}
