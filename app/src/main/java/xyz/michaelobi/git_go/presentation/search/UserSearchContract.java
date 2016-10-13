package xyz.michaelobi.git_go.presentation.search;

import java.util.List;

import xyz.michaelobi.git_go.data.remote.model.User;
import xyz.michaelobi.mvp.Mvp;

/**
 * Created by Michael on 10/12/2016.
 */

public interface UserSearchContract {
    interface View extends Mvp.View {
        void showSearchResults(List<User> githubUserList);

        void showError(String error);

        void showLoading();

        void hideLoading();
    }

    interface Presenter extends Mvp.Presenter<View> {
        void search(String term);
    }
}
