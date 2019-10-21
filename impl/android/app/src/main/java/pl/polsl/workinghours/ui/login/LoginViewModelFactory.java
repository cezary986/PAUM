package pl.polsl.workinghours.ui.login;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import pl.polsl.workinghours.data.RequestQueueProvider;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.login.LoginDataSource;
import pl.polsl.workinghours.data.login.LoginRepository;

/**
 * ViewModel provider factory to instantiate UserViewModel.
 * Required given UserViewModel has a non-empty constructor
 */
public class LoginViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Context context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application
     */
    public LoginViewModelFactory(@NonNull Application application) {
        super(application);

        this.context = application.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            RequestQueueProvider requestQueueProvider = RequestQueueProvider.getInstance(this.context);
            CredentialDataSource credentialDataSource = new CredentialDataSource();
            return (T) new LoginViewModel(
                    LoginRepository.getInstance(
                            new LoginDataSource(
                                    requestQueueProvider, credentialDataSource
                            ), credentialDataSource));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
