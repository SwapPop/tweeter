package edu.byu.cs.tweeter.server.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactoryProvider;
import edu.byu.cs.tweeter.server.dao.UserDAO;

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

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an AuthToken");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new GetFollowingCountResponse("Session expired");
        }
        return getUserDAO().getFollowingCount(request);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a target user alias");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have an AuthToken");
        }
        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new GetFollowersCountResponse("Session expired");
        }
        return getUserDAO().getFollowersCount(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[BadRequest] Missing an alias");
        }

        if(!getAuthTokenDAO().validateAuthToken(request.getAuthToken())){
            return new GetUserResponse("Session expired");
        }
        return getUserDAO().findUser(request);
    }

    public static String hashPassword(String passwordToHash) {
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

    //TO BE USED IN TESTING
    public String getByteArrayFromImageURL(String url) {

        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    UserDAO getUserDAO() {
        return daoProvider.getDaoFactory().getUserDAO();
    }
    AuthTokenDAO getAuthTokenDAO() {
        return daoProvider.getDaoFactory().getAuthTokenDAO();
    }

}
