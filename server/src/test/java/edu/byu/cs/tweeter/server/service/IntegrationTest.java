package edu.byu.cs.tweeter.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryProvider;
import edu.byu.cs.tweeter.server.dao.FollowDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAODynamoDB;

public class IntegrationTest {

    private RegisterRequest request;
    private DAOFactory factory;
    private UserDAO userDAO;
    private UserService userServiceSpy;
    private StatusService statusService;
    Status expectedPost;
    User currentUser;
//    User currentUser = new User("Jake", "Taylor", "https://jtay11-tweeter-images.s3.amazonaws.com/License.jpg");

    @Before
    public void setup() {
        String lastName = UUID.randomUUID().toString().substring(0, 5);
        currentUser = new User("Jake", lastName, "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        factory = new DAOFactoryProvider().getDaoFactory();
        userDAO = factory.getUserDAO();
        userServiceSpy = Mockito.spy(UserService.class);
        Mockito.when(userServiceSpy.getUserDAO()).thenReturn(userDAO);

        statusService = new StatusService();

        String imageString = userServiceSpy.getByteArrayFromImageURL(currentUser.getImageUrl());
        // Setup a request object to use in the tests
        request = new RegisterRequest(currentUser.getAlias(), "password", currentUser.getFirstName(), currentUser.getLastName(), imageString);


        List<String> urls = new ArrayList<>();
        urls.add("amazon.com");
        List<String> mentions = new ArrayList<>();
        mentions.add("@friend1");

        expectedPost = new Status("TEST POST!!! @friend1", currentUser, new Date().toString(), urls, mentions);

    }

    @Test
    public void LoginAndPostIntegrationTest() {

        AuthResponse response = userServiceSpy.register(request);
        Assert.assertEquals(currentUser, response.getUser());
        Assert.assertEquals(request.getImage(), userServiceSpy.getByteArrayFromImageURL(response.getUser().getImageUrl()));

        AuthResponse loginResponse = userServiceSpy.login(new LoginRequest(currentUser.getAlias(), "password"));
        Assert.assertEquals(currentUser, loginResponse.getUser());
        Assert.assertNotNull(loginResponse.getAuthToken());

        PostStatusResponse postResponse = statusService.postStatus(new PostStatusRequest(loginResponse.getAuthToken(), expectedPost));
        Assert.assertEquals("Successfully Posted!", postResponse.getMessage());
        StoryResponse storyResponse = statusService.getStory(new StoryRequest(loginResponse.getAuthToken(), currentUser.getAlias(), 10, null));
        Assert.assertEquals(expectedPost, storyResponse.getStory().get(0));
    }
}
