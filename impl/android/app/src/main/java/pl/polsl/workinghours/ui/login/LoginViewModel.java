package pl.polsl.workinghours.ui.login;
import android.content.Context;
import androidx.lifecycle.ViewModel;
import pl.polsl.workinghours.R;
import pl.polsl.workinghours.data.login.LoginRepository;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.TokenRefreshResponse;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";

    private BehaviorSubject<LoginFormState> loginFormState = BehaviorSubject.create();

    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    Observable<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public Observable<LoginResponse> login(String username, String password, Context context) {
        return this.loginRepository.login(username, password, context);
    }

    public Observable<TokenRefreshResponse> seamlesslyLogin(Context context) {
        return this.loginRepository.refreshToken(context);
    }

    public void logout(Context context) {
        this.loginRepository.logout(context);
    }

    void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.onNext(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.onNext(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.onNext(new LoginFormState(true));
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
