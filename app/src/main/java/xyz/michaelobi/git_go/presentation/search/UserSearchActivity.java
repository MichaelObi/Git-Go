package xyz.michaelobi.git_go.presentation.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xyz.michaelobi.git_go.R;
import xyz.michaelobi.git_go.data.remote.model.User;

public class UserSearchActivity extends AppCompatActivity implements UserSearchContract.View {

    UserSearchContract.Presenter userSearchPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        userSearchPresenter = new UserSearchPresenter(Injection.provideUserRepo(), Schedulers.io(), AndroidSchedulers
                .mainThread());
        userSearchPresenter.attachView(this);
    }

    @Override
    public void showSearchResults(List<User> githubUserList) {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
