package pl.polsl.workinghours.data.login;
import android.accounts.AuthenticatorException;
import android.content.Context;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.TokenRefreshResponse;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private LoginDataSource loginDataSource;
    private CredentialDataSource credentialDataSource;

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
            credentialDataSource.clear(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Observable<LoginResponse> login(String username, String password, Context context) {
        return this.loginDataSource.login(username, password, context);
    }

    public Observable<TokenRefreshResponse> refreshToken(Context context) {
        try {
            return this.loginDataSource.login(this.credentialDataSource.getRefreshToken(context));
        } catch (Exception e) {
            BehaviorSubject<TokenRefreshResponse> subject = BehaviorSubject.create();
            e.printStackTrace();
            subject.onError(new AuthenticatorException("Failed to refresh token"));
            return subject;
        }
    }
}
