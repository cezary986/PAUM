package pl.polsl.workinghours.data.user;

import androidx.lifecycle.MutableLiveData;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.ui.login.DataWrapper;

public class UserRepository {

    private static volatile UserRepository instance;

    private UserDataSource userDataSource;
    private AuthRepository authRepository;

    public UserRepository(
            UserDataSource userDataSource,
            AuthRepository authRepository) {
        this.userDataSource = userDataSource;
        this.authRepository = authRepository;
    }

    public UserRepository getInstance(UserDataSource userDataSource, AuthRepository authRepository) {
        if (instance == null) {
            instance = new UserRepository(userDataSource, authRepository);
        }
        return instance;
    }

    public void getUserGroups(MutableLiveData<DataWrapper<String[]>> result) {
        this.userDataSource.getUserGroups(
                this.authRepository.getStoredAccessToken(),
                result
        );
    }

    public void getUser(MutableLiveData<DataWrapper<User>> result) {
        this.userDataSource.getProfile(
                this.authRepository.getStoredAccessToken(),
                result
        );
    }
}
