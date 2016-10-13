package xyz.michaelobi.git_go.data;

import java.util.List;

import rx.Observable;
import xyz.michaelobi.git_go.data.remote.model.User;


public interface UserRepositoryContract {
    Observable<List<User>> searchUsers(final String searchTerm);
}
