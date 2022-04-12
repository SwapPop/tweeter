package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAO {
    boolean validateAuthToken(AuthToken authToken);
    AuthToken addAuthToken(String alias);
    AuthToken refreshToken(AuthToken token, String alias);
    boolean removeAuthToken(AuthToken token);

    String getAliasFromToken(AuthToken authToken);
}
