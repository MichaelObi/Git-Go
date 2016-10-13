package xyz.michaelobi.git_go.data;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import xyz.michaelobi.git_go.data.remote.GithubUserRestService;
import xyz.michaelobi.git_go.data.remote.model.User;

/**
 * Created by Michael on 9/27/2016.
 */

public class UserRepository implements UserRepositoryContract {

    private GithubUserRestService githubUserRestService;

    public UserRepository(GithubUserRestService githubUserRestService) {
        this.githubUserRestService = githubUserRestService;
    }

    @Override
    public Observable<List<User>> searchUsers(final String searchTerm) {
        return Observable.defer(() -> githubUserRestService.searchGithubUsers(searchTerm)
                .concatMap(userList -> Observable.from(userList.getItems())
                        .concatMap(user -> githubUserRestService.getUser(user.getLogin())).toList()))
                .retryWhen(observable -> observable.flatMap(o -> {
                            if (o instanceof IOException) {
                                return Observable.just(null);
                            }
                            return Observable.error(o);
                        })
                );
    }

}
