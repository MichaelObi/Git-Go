package xyz.michaelobi.git_go.presentation.search;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import xyz.michaelobi.git_go.data.UserRepositoryContract;
import xyz.michaelobi.git_go.data.remote.model.User;
import xyz.michaelobi.git_go.data.remote.model.UsersList;
import xyz.michaelobi.mvp.BasePresenter;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Michael on 10/12/2016.
 */
public class UserSearchPresenterTest {

    private static final String USER_LOGIN_MICHAELOBI = "michaelobi";
    private static final String USER_LOGIN_RIGGAROO = "riggaroo";
    @Mock
    UserRepositoryContract userRepository;

    @Mock
    UserSearchContract.View view;

    UserSearchPresenter userSearchPresenter;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        userSearchPresenter = new UserSearchPresenter(userRepository, Schedulers.immediate(), Schedulers.immediate());
        userSearchPresenter.attachView(view);
    }

    @Test
    public void search_validSearchTerm_returnsResult() throws Exception {
        UsersList usersList = getDummyUserList();
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.<List<User>>just(usersList.getItems()));

        userSearchPresenter.search("michaelobi");
        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view, never()).showError(anyString());
        verify(view).showSearchResults(usersList.getItems());
    }

    @Test
    public void search_userRepositoryError_returnErrorMsg() {
        String errorMsg = "Internet access failed";
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.error(new IOException(errorMsg)));

        userSearchPresenter.search("google");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).showError(errorMsg);
        verify(view, never()).showSearchResults(anyList());
    }

    @Test(expected = BasePresenter.MvpViewNotAttachedException.class)
    public void search_viewNotAttached_throwMvpViewException() {
        userSearchPresenter.detachView();

        userSearchPresenter.search("test");

        verify(view, never()).showLoading();
        verify(view, never()).showSearchResults(anyList());
    }

    UsersList getDummyUserList() {
        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user1FullDetails());
        githubUsers.add(user2FullDetails());
        return new UsersList(githubUsers);
    }

    User user1FullDetails() {
        return new User(USER_LOGIN_RIGGAROO, "Rigs Franks", "avatar_url", "Bio1");
    }

    User user2FullDetails() {
        return new User(USER_LOGIN_MICHAELOBI, "Michael Obi", "avatar_url2", "Bio2");
    }
}