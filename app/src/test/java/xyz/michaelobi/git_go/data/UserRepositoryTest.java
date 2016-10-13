package xyz.michaelobi.git_go.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;
import xyz.michaelobi.git_go.data.remote.GithubUserRestService;
import xyz.michaelobi.git_go.data.remote.model.User;
import xyz.michaelobi.git_go.data.remote.model.UsersList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {

    private static final String USER_LOGIN_MICHAELOBI = "michaelobi";
    private static final String USER_LOGIN_RIGGAROO = "riggaroo";

    @Mock
    GithubUserRestService githubUserRestService;

    private UserRepositoryContract userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepository = new UserRepository(githubUserRestService);
    }

    @Test
    public void searchUsers_200OkResponse_invokesCorrectApiCalls() {
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails(), user2FullDetails()));

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGGAROO).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        List<List<User>> onNextEvents = subscriber.getOnNextEvents();
        List<User> users = onNextEvents.get(0);
        Assert.assertEquals(USER_LOGIN_RIGGAROO, users.get(0).getLogin());
        Assert.assertEquals(USER_LOGIN_MICHAELOBI, users.get(1).getLogin());
        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_RIGGAROO);
        verify(githubUserRestService).getUser(USER_LOGIN_RIGGAROO);
        verify(githubUserRestService).getUser(USER_LOGIN_MICHAELOBI);
    }

    private UsersList githubUserList() {
        User user = new User();
        user.setLogin(USER_LOGIN_RIGGAROO);

        User user2 = new User();
        user2.setLogin(USER_LOGIN_MICHAELOBI);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        return new UsersList(users);
    }

    private User user1FullDetails() {
        User user = new User();
        user.setLogin(USER_LOGIN_RIGGAROO);
        user.setName("Rigs Franks");
        user.setAvatarUrl("avatar_url");
        user.setBio("Bio1");
        return user;
    }

    private User user2FullDetails() {
        User user = new User();
        user.setLogin(USER_LOGIN_MICHAELOBI);
        user.setName("Michael Obi");
        user.setAvatarUrl("avatar_url2");
        user.setBio("Bio2");
        return user;
    }

    @Test
    public void searchUsers_IOExceptionThenSuccess_SearchUsersRetried() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails()), Observable.just(user2FullDetails()));

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGGAROO).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_RIGGAROO);

        verify(githubUserRestService).getUser(USER_LOGIN_RIGGAROO);
        verify(githubUserRestService).getUser(USER_LOGIN_MICHAELOBI);
    }

    @Test
    public void searchUsers_GetUserIOExceptionThenSuccess_SearchUsersRetried() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(user1FullDetails()),
                        Observable.just(user2FullDetails()));

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGGAROO).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_RIGGAROO);

        verify(githubUserRestService, times(2)).getUser(USER_LOGIN_RIGGAROO);
        verify(githubUserRestService).getUser(USER_LOGIN_MICHAELOBI);
    }

    @Test
    public void searchUsers_OtherHttpError_SearchTerminatedWithError() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString())).thenReturn(get403ForbiddenError());

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGGAROO).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);

        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_RIGGAROO);

        verify(githubUserRestService, never()).getUser(USER_LOGIN_RIGGAROO);
        verify(githubUserRestService, never()).getUser(USER_LOGIN_MICHAELOBI);
    }


    private Observable getIOExceptionError() {
        return Observable.error(new IOException());
    }

    private Observable<UsersList> get403ForbiddenError() {
        return Observable.error(new HttpException(
                Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "Forbidden"))));
    }
}