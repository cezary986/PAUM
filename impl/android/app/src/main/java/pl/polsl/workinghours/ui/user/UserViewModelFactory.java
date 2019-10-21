package pl.polsl.workinghours.ui.user;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pl.polsl.workinghours.data.RequestQueueProvider;
import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.login.LoginDataSource;
import pl.polsl.workinghours.data.login.LoginRepository;
import pl.polsl.workinghours.data.user.UserDataSource;
import pl.polsl.workinghours.data.user.UserRepository;

/**
 * ViewModel provider factory to instantiate UserViewModel.
 * Required given UserViewModel has a non-empty constructor
 */
public class UserViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Context context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application
     */
    public UserViewModelFactory(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            RequestQueueProvider requestQueueProvider = RequestQueueProvider.getInstance(this.context);
            return (T) new UserViewModel(
                    new UserRepository(
                            new UserDataSource(RequestQueueProvider.getInstance(context)
                    ), AuthRepository.getInstance(new CredentialDataSource(), RequestQueueProvider.getInstance(context))
            ));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
