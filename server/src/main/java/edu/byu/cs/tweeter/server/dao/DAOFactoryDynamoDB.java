package edu.byu.cs.tweeter.server.dao;

public class DAOFactoryDynamoDB implements DAOFactory{

    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAODynamoDB();
    }

    @Override
    public StatusDAO getStatusDAO() {
        return new StatusDAODynamoDB();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAODynamoDB();
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAODynamoDB();
    }
}
