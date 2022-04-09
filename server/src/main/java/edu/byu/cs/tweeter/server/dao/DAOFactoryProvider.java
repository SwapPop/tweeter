package edu.byu.cs.tweeter.server.dao;

public class DAOFactoryProvider {
    public DAOFactory getDaoFactory(){
        return new DAOFactoryDynamoDB();
    }
}
