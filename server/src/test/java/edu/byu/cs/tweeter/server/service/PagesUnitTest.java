package edu.byu.cs.tweeter.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class PagesUnitTest {
    private StatusDAO mockDAO;
    private StatusService statusServiceSpy;
    private User currentUser;
    private AuthTokenDAO mockAuthDAO;

    @Before
    public void setup() {
        mockDAO = Mockito.mock(StatusDAO.class);
        mockAuthDAO = Mockito.mock(AuthTokenDAO.class);
        statusServiceSpy = Mockito.spy(new StatusService());

        currentUser = new User("Jake", "Taylor", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

        Mockito.when(statusServiceSpy.getStatusDAO()).thenReturn(mockDAO);
    }

    @Test
    public void getStoryPagesTest() {
//        Answer<StoryResponse> answer = new Answer<>() {
//            @Override
//            public StoryResponse answer(InvocationOnMock invocation) throws Throwable {
//
//            }
//        };
        List<Status> story = new ArrayList<>();
        for (int i = 0; i < 50; i++){
            story.add(new Status("TEST" + i, new User("Jake", String.valueOf(i), "https://picsum.photos/id/237/300/300"), new Date().toString(), null, null));
        }
        StoryResponse storyResponse = new StoryResponse(story, true);

        AuthToken authToken = new AuthToken();

        Mockito.when(statusServiceSpy.getAuthTokenDAO()).thenReturn(mockAuthDAO);
        Mockito.when(mockAuthDAO.validateAuthToken(authToken)).thenReturn(true);

        Mockito.when(statusServiceSpy.getStory(new StoryRequest(authToken, currentUser.getAlias(), 10, null))).thenReturn(storyResponse);

        StoryResponse response = statusServiceSpy.getStory(new StoryRequest(authToken, currentUser.getAlias(), 10, null));
        Assert.assertEquals(response.getStory().size(), 10);


    }
}
