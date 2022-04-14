package edu.byu.cs.tweeter.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAODynamoDB;
import edu.byu.cs.tweeter.server.dao.FollowDAODynamoDB;

public class StatusServiceTest {

    private PostStatusRequest request;
    private FollowingResponse expectedResponse;
    private FollowDAODynamoDB mockFollowDAO;
    private StatusService statusService;

    @Before
    public void setup() {
        AuthTokenDAODynamoDB aDAO = new AuthTokenDAODynamoDB();

        AuthToken authToken = aDAO.addAuthToken("@jt");

        User currentUser = new User("Jake", "Taylor", "@jt", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
//        this.post = post;
//        this.user = user;
//        this.datetime = datetime;
//        this.urls = urls;
//        this.mentions = mentions;
        List<String> mentions = new ArrayList<>();
        List<String> urls = new ArrayList<>();

        mentions.add("@friend1");
        urls.add("amazon.com");
        request = new PostStatusRequest(authToken, new Status("Test Post from StatusServiceTest! @friend1 amazon.com", currentUser, new Date().toString(), urls, mentions));

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new FollowingResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
        mockFollowDAO = Mockito.mock(FollowDAODynamoDB.class);
//        Mockito.when(mockFollowDAO.getFollowees(request)).thenReturn(expectedResponse);

        statusService = new StatusService();
    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link FollowDAODynamoDB} class.
     */
    @Test
    public void testPostStatus_validRequest_correctResponse() {
        PostStatusResponse response = statusService.postStatus(request);
        Assert.assertTrue(response.isSuccess());
    }
}
