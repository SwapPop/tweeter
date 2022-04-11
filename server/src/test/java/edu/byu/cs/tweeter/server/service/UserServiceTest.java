package edu.byu.cs.tweeter.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAODynamoDB;

public class UserServiceTest {

    private RegisterRequest request;
    private AuthResponse expectedResponse;
    private UserDAODynamoDB mockUserDAO;
    private UserService userServiceSpy;
    private UserService userService;
//    User currentUser = new User("Jake", "Taylor", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
    User currentUser = new User("Jake", "Taylor", "https://jtay11-tweeter-images.s3.amazonaws.com/License.jpg");

    @Before
    public void setup() {
        AuthToken authToken = new AuthToken();
        userService = new UserService();

        String imageString = userService.getByteArrayFromImageURL(currentUser.getImageUrl());
        // Setup a request object to use in the tests
        request = new RegisterRequest(currentUser.getAlias(), "password", currentUser.getFirstName(), currentUser.getLastName(), imageString);

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new AuthResponse(currentUser, authToken);

    }

    @Test
    public void testMockRegister_validRequest_correctResponse() {
        mockUserDAO = Mockito.mock(UserDAODynamoDB.class);
        Mockito.when(mockUserDAO.register(request)).thenReturn(expectedResponse);
        Mockito.when(mockUserDAO.availableAlias(currentUser.getAlias())).thenReturn(true);

        userServiceSpy = Mockito.spy(UserService.class);
        Mockito.when(userServiceSpy.getUserDAO()).thenReturn(mockUserDAO);

        AuthResponse response = userServiceSpy.register(request);
        Assert.assertEquals(expectedResponse, response);
    }

    @Test
    public void testRegister_validRequest_correctResponse() {
        AuthResponse response = userService.register(request);
        Assert.assertEquals(expectedResponse.getUser(), response.getUser());
        Assert.assertEquals(request.getImage(), userService.getByteArrayFromImageURL(response.getUser().getImageUrl()));
    }
}
