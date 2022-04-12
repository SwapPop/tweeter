package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {
    private User currentUser;
    private AuthToken currentAuthToken;

    private ServerFacade serverFacade;

    //private CountDownLatch countDownLatch;

    /**
     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @Before
    public void setup() {
        currentUser = new User("FirstName", "LastName", getFakeData().getFirstUser().getImageUrl());
        currentAuthToken = new AuthToken();
        serverFacade = new ServerFacade();
    }

    public FakeData getFakeData() {
        return new FakeData();
    }

    @Test
    public void testSuccessfulRegister() throws IOException, TweeterRemoteException {
        AuthResponse testResponse = new AuthResponse(getFakeData().getFirstUser(), getFakeData().getAuthToken());
        RegisterRequest request = new RegisterRequest(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getAlias(), "password", currentUser.getImageUrl());
        AuthResponse response = serverFacade.register(request, "/register");
        Assert.assertEquals(response.getUser(), testResponse.getUser());
        Assert.assertEquals(response.getAuthToken().getToken(), testResponse.getAuthToken().getToken());
        Assert.assertEquals(response.isSuccess(), testResponse.isSuccess());
    }

    @Test
    public void testSuccessfulGetFollowers() throws IOException, TweeterRemoteException {
        FollowersResponse testResponse = new FollowersResponse(getFakeData().getFakeUsers().subList(0,10), true);
        FollowersRequest request = new FollowersRequest(getFakeData().getAuthToken(), getFakeData().getFirstUser().getAlias(), 10, null);
        FollowersResponse response = serverFacade.getFollowers(request, "/getfollowers");
        Assert.assertEquals(response.getFollowers(), testResponse.getFollowers());
        Assert.assertEquals(response.getHasMorePages(), testResponse.getHasMorePages());
        Assert.assertEquals(response.isSuccess(), testResponse.isSuccess());
    }

    @Test
    public void testSuccessfulGetFollowerCount() throws IOException, TweeterRemoteException {
        GetFollowersCountResponse testResponse = new GetFollowersCountResponse(21);
        GetFollowersCountRequest request = new GetFollowersCountRequest(getFakeData().getFirstUser().getAlias(), currentAuthToken);
        GetFollowersCountResponse response = serverFacade.getFollowersCount(request, "/getfollowerscount");
        Assert.assertEquals(response.getCount(), testResponse.getCount());
        Assert.assertEquals(response.isSuccess(), testResponse.isSuccess());
    }
}
