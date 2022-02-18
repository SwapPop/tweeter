package edu.byu.cs.tweeter.client.presenter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private UserService mockUserService;
    private StatusService mockStatusService;
    private Cache mockCache;

    MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        mockView = Mockito.mock(MainPresenter.View.class);
        mockUserService = Mockito.mock(UserService.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        //works with void methods, always returns
//        Mockito.doReturn(mockUserService).when(mainPresenterSpy).getUserService();
        //has to return, offers type checking
        Mockito.when(mainPresenterSpy.getUserService()).thenReturn(mockUserService);
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        Cache.setInstance(mockCache);

    }

    @Test
    public void testLogout_success() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.LogoutObserver observer = (MainPresenter.LogoutObserver) invocation.getArgument(0, MainPresenter.LogoutObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        startLogout(answer);

        Mockito.verify(mockCache).clearCache();

        Mockito.verify(mockView).cancelLogoutToast();
        Mockito.verify(mockView).logoutUser();

    }

    @Test
    public void testLogout_fail() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.LogoutObserver observer = (MainPresenter.LogoutObserver) invocation.getArgument(0, MainPresenter.LogoutObserver.class);
                observer.handleFailure("FAILURE MESSAGE");
                return null;
            }
        };

        startLogout(answer);

        verifyLogoutFailureResult("Failed to logout: FAILURE MESSAGE");

    }

    @Test
    public void testLogout_exception() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainPresenter.LogoutObserver observer = (MainPresenter.LogoutObserver) invocation.getArgument(0, MainPresenter.LogoutObserver.class);
                observer.handleException(new Exception("EXCEPTION MESSAGE"));
                return null;
            }
        };

        startLogout(answer);

        verifyLogoutFailureResult("Failed to logout because of exception: EXCEPTION MESSAGE");
    }



    @Test
    public void testPost_success() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("Test post", invocation.getArgument(0, Status.class).getPost());
                MainPresenter.PostStatusObserver observer = (MainPresenter.PostStatusObserver) invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        startPost(answer);

        Mockito.verify(mockView).cancelPostToast();
        Mockito.verify(mockView).displayMessage("Successfully Posted!");

    }

    @Test
    public void testPost_fail() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("Test post", invocation.getArgument(0, Status.class).getPost());
                MainPresenter.PostStatusObserver observer = (MainPresenter.PostStatusObserver) invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                observer.handleFailure("FAILURE MESSAGE");
                return null;
            }
        };

        startPost(answer);

        verifyPostFailureResult("Failed to post status: FAILURE MESSAGE");

    }

    @Test
    public void testPost_exception() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("Test post", invocation.getArgument(0, Status.class).getPost());
                MainPresenter.PostStatusObserver observer = (MainPresenter.PostStatusObserver) invocation.getArgument(1, MainPresenter.PostStatusObserver.class);
                observer.handleException(new Exception("EXCEPTION MESSAGE"));
                return null;
            }
        };

        startPost(answer);

        verifyPostFailureResult("Failed to post status because of exception: EXCEPTION MESSAGE");
    }

    private void startLogout(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockUserService).logout(Mockito.any());
        mainPresenterSpy.logout();
        Mockito.verify(mockView).showLogoutToast();
    }

    private void startPost(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.post("Test post");
        Mockito.verify(mockView).showPostToast();
    }

    private void verifyLogoutFailureResult(String s) {
        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
        Mockito.verify(mockView).displayMessage(s);
    }

    private void verifyPostFailureResult(String s) {
        Mockito.verify(mockView).displayMessage(s);
    }
}
