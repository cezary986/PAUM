package pl.polsl.workinghours.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import org.json.JSONException;

import pl.polsl.workinghours.data.login.LoginRepository;
import pl.polsl.workinghours.data.login.Result;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.R;

public class LoginViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<LoginResponse>> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;

        this.loginResult = new MutableLiveData<>();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<DataWrapper<LoginResponse>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password, Context context) {
        try {
            this.loginResult = loginRepository.login(username, password, context, loginResult);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf(TAG, e.getMessage());
        }
    }

    void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return !username.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }
}
