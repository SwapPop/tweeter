package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface UserDAO {
    AuthResponse login(LoginRequest request);

    LogoutResponse logout(LogoutRequest request);

    AuthResponse register(RegisterRequest request);

    GetUserResponse findUser(GetUserRequest request);

    GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request);

    GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request);

    boolean availableAlias(String username);

    void increaseFollowCounts(FollowRequest request, String userAlias);

    void decreaseFollowCounts(UnfollowRequest request, String userAlias);

    User getUserByAlias(String followeeHandle);
}
