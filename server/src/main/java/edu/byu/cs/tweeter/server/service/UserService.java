package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    public AuthResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        return getUserDAO().login(request);
    }

    public AuthResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing an image");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        return getUserDAO().register(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing an alias");
        }
        // TODO: Generates dummy data. Replace with a real implementation.
        return getUserDAO().findUser(request);
    }

    UserDAO getUserDAO() {
        return new UserDAO();
    }

}
