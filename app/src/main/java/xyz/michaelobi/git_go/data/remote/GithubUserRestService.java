package xyz.michaelobi.git_go.data.remote;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import xyz.michaelobi.git_go.data.remote.model.User;
import xyz.michaelobi.git_go.data.remote.model.UsersList;

/**
 * Created by Michael on 9/27/2016.
 */

public interface GithubUserRestService {
    @GET("/search/users?per_page=2")
    Observable<UsersList> searchGithubUsers(@Query("q") String searchTerm);

    @GET("/users/{username}")
    Observable<User> getUser(@Path("username") String username);
}
