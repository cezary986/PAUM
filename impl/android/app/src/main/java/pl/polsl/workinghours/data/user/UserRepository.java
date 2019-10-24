package pl.polsl.workinghours.data.user;

import android.accounts.AuthenticatorException;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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

    public void getUserGroups(MutableLiveData<DataWrapper<String[]>> result, Context context) {
        Observer<DataWrapper<String>> tokenObserver = stringDataWrapper -> {
            if (stringDataWrapper.isOk()) {
                String accessToken = stringDataWrapper.getSuccess();
                userDataSource.getUserGroups(accessToken, result);
            } else {
                //TODO  Handle error
            }
        };
        MutableLiveData<DataWrapper<String>> mutableLiveData = null;
        try {
            mutableLiveData = this.authRepository.getAccessToken(context);
            mutableLiveData.observeForever(tokenObserver);
        } catch (AuthenticatorException e) {
            //TODO  Handle error
        } finally {
            if (mutableLiveData != null)
                mutableLiveData.removeObserver(tokenObserver);
        }
    }

    public void getUser(MutableLiveData<DataWrapper<User>> result, Context context) {
        Observer<DataWrapper<String>> tokenObserver = stringDataWrapper -> {
            if (stringDataWrapper.isOk()) {
                String accessToken = stringDataWrapper.getSuccess();
                userDataSource.getProfile(accessToken, result);
            } else {
                //TODO  Handle error
            }
        };
        MutableLiveData<DataWrapper<String>> mutableLiveData = null;
        try {
            mutableLiveData = this.authRepository.getAccessToken(context);
            mutableLiveData.observeForever(tokenObserver);
        } catch (AuthenticatorException e) {
            //TODO  Handle error
        } finally {
            if (mutableLiveData != null)
                mutableLiveData.removeObserver(tokenObserver);
        }
    }
}
