package pl.polsl.workinghours.data.auth;
import android.accounts.AuthenticatorException;
import android.content.Context;
import com.auth0.android.jwt.JWT;

import pl.polsl.workinghours.data.login.LoginDataSource;
import pl.polsl.workinghours.data.login.LoginRepository;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AuthRepository {

    private static volatile AuthRepository instance;
    private CredentialDataSource dataSource;
    private String accessToken = null;
    private String refreshToken = null;

    // private constructor : singleton access
    private AuthRepository(CredentialDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static AuthRepository getInstance(CredentialDataSource dataSource) {
        if (instance == null) {
            instance = new AuthRepository(dataSource);
        }
        return instance;
    }

    /**
     * Metoda zwracająca access token
     *
     * @param context kontekt
     * @return opakowanie z access kodem w środku
     * @throws AuthenticatorException gdy nie ma access tokena ani refresh tokena
     * by pobrac nowy
     */
    public Observable<String> getAccessToken(Context context) throws AuthenticatorException {
        if (this.accessToken != null && !this.isAccessTokenExpired()) {
            BehaviorSubject<String> subject = BehaviorSubject.create();
            subject.onNext(this.accessToken);
            return subject;
        } else {
            // nie mamy tokena lub się przedawnił
            try {
                String refreshToken = this.dataSource.getRefreshToken(context);
                LoginRepository loginRepository = LoginRepository.getInstance(
                        new LoginDataSource(dataSource),
                        dataSource
                );
                return loginRepository.refreshToken(context).first().map(response -> response.access);
            } catch (Exception e) {
                throw new AuthenticatorException();
            }
        }
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public void setRefreshToken(String refreshToken, Context context) {
        this.refreshToken = refreshToken;
        this.dataSource.saveRefreshToken(refreshToken, context);
    }

    private boolean isAccessTokenExpired() {
        JWT jwt = new JWT(this.accessToken);
        return jwt.isExpired(5);
    }
}
