package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactoryProvider;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAODynamoDB;

public class UserService {

    DAOFactoryProvider daoProvider;

    public UserService() {
        this.daoProvider = new DAOFactoryProvider();
    }

    public AuthResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        String hashedPassword = hashPassword(request.getPassword());
        request.setPassword(hashedPassword);

        AuthResponse response = getUserDAO().login(request);
        if(!response.isSuccess()) {
            return new AuthResponse("Failed to login");
        }
        return response;
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[BadRequest] Missing an authToken");
        }

        return getUserDAO().logout(request);
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

        boolean available = getUserDAO().availableAlias(request.getUsername());
        if (!available) {
            return new AuthResponse("Alias already taken!");
        }

        String hashedPassword = hashPassword(request.getPassword());
        if (hashedPassword.equals("FAILED TO HASH")) {
            return new AuthResponse("Password failed to hash, please try again");
        }
        request.setPassword(hashedPassword);

        AuthResponse response = getUserDAO().register(request);
        if(!response.isSuccess()) {
            return new AuthResponse("Failed to register");
        }
        return response;
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing an alias");
        }
        return getUserDAO().findUser(request);
    }

    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }

    UserDAO getUserDAO() {
        return daoProvider.getDaoFactory().getUserDAO();
    }

}
