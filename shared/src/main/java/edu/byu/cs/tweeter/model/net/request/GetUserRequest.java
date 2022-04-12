package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class GetUserRequest {

    private String alias;
    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private GetUserRequest() {}

    public GetUserRequest(String alias, AuthToken authToken) {
        this.alias = alias;
        this.authToken = authToken;
    }

    /**
     * Returns the alias/username of the user to be found in by this request.
     *
     * @return the alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias/username.
     *
     * @param alias the alias.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
