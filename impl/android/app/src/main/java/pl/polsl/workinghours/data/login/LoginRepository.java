package pl.polsl.workinghours.data.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;

import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.ui.login.DataWrapper;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private LoginDataSource loginDataSource;
    private CredentialDataSource credentialDataSource;

    private String accessToken;
    private String refreshToken;

    private MutableLiveData<DataWrapper<LoginResponse>> loginResult;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource loginDataSource, CredentialDataSource credentialDataSource) {
        this.loginDataSource = loginDataSource;
        this.credentialDataSource = credentialDataSource;
    }

    public static LoginRepository getInstance(
            LoginDataSource loginDataSource,
            CredentialDataSource credentialDataSource) {
        if (instance == null) {
            instance = new LoginRepository(loginDataSource, credentialDataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return this.credentialDataSource.getAccessToken() != null;
    }

    public void logout(Context context) {
        try {
            loginDataSource.logout(this.credentialDataSource.getRefreshToken(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<DataWrapper<LoginResponse>> login(String username, String password, Context context, MutableLiveData<DataWrapper<LoginResponse>> result) throws JSONException {
        if (this.loginResult == null) {
            this.loginResult = new MutableLiveData<>();
        }
        this.loginDataSource.login(username, password, context, result);
        return this.loginResult;
    }
}
