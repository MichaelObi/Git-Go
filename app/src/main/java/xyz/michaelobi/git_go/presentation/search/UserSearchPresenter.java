package xyz.michaelobi.git_go.presentation.search;

import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import xyz.michaelobi.git_go.data.UserRepositoryContract;
import xyz.michaelobi.git_go.data.remote.model.User;
import xyz.michaelobi.mvp.BasePresenter;

/**
 * Created by Michael on 10/12/2016.
 */

public class UserSearchPresenter extends BasePresenter<UserSearchContract.View> implements UserSearchContract
        .Presenter {

    private final Scheduler ioScheduler;
    private final Scheduler mainScheduler;
    private UserRepositoryContract userRepository;

    UserSearchPresenter(UserRepositoryContract userRepository, Scheduler ioScheduler, Scheduler mainScheduler) {
        this.userRepository = userRepository;
        this.ioScheduler = ioScheduler;
        this.mainScheduler = mainScheduler;
    }

    @Override
    public void search(String term) {
        checkViewAttached();
        getView().showLoading();
        addSubscription(userRepository.searchUsers(term).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(
                new Subscriber<List<User>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideLoading();
                        getView().showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<User> users) {
                        getView().hideLoading();
                        getView().showSearchResults(users);
                    }
                })
        );
    }
}
