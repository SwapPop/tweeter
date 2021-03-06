package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    FollowDAO getFollowDAO();
    StatusDAO getStatusDAO();
    UserDAO getUserDAO();
    AuthTokenDAO getAuthTokenDAO();
}
