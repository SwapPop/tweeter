package edu.byu.cs.tweeter.server.dao;

public class DAOFactoryProvider {
    public DAOFactory getDaoFactory(){
        //current implementation operates on DynamoDB
        return new DAOFactoryDynamoDB();
    }
}
