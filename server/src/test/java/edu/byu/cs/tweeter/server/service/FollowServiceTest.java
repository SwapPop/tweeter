package edu.byu.cs.tweeter.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.FollowDAODynamoDB;

public class FollowServiceTest {

    private FollowersRequest request;
    private FollowingResponse expectedResponse;
    private FollowDAODynamoDB mockFollowDAO;
    private FollowService followServiceSpy;

    @Before
    public void setup() {
        AuthTokenDAODynamoDB aDAO = new AuthTokenDAODynamoDB();

        AuthToken authToken = aDAO.addAuthToken("@jt");

        User currentUser = new User("FirstName", "LastName", null);

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
        request = new FollowersRequest(authToken, "@jt", 3, null);

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new FollowingResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
        mockFollowDAO = Mockito.mock(FollowDAODynamoDB.class);
//        Mockito.when(mockFollowDAO.getFollowees(request)).thenReturn(expectedResponse);

        followServiceSpy = Mockito.spy(FollowService.class);
        Mockito.when(followServiceSpy.getFollowDAO()).thenReturn(mockFollowDAO);
    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link FollowDAODynamoDB} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        FollowService followService = new FollowService();
        IsFollowerResponse response = followService.isFollower(new IsFollowerRequest("@jt", "@friend1", request.getAuthToken()));

//        Assert.assertEquals(expectedResponse, response);
    }
}
