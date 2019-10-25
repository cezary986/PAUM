package pl.polsl.workinghours.ui.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.user.UserDataSource;
import pl.polsl.workinghours.data.user.UserRepository;

/**
 * ViewModel provider factory to instantiate UserViewModel.
 * Required given UserViewModel has a non-empty constructor
 */
public class UserViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application
     */
    public UserViewModelFactory(@NonNull Application application) {
        super(application);

        this.context = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(
                    this.context,
                    UserRepository.getInstance(
                            new UserDataSource(),
                            AuthRepository.getInstance(new CredentialDataSource())
                    ));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
