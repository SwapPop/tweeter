package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class GetUserResponse extends Response {

    private User user;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public GetUserResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the found user
     */
    public GetUserResponse(User user) {
        super(true, null);
        this.user = user;
    }

    /**
     * Returns the found user.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }
}
