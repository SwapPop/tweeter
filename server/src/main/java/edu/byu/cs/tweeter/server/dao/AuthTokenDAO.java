package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.response.AuthResponse;

public interface AuthTokenDAO {
    boolean addToken(AuthResponse response);
}
