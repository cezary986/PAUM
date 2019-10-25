package pl.polsl.workinghours.data.login;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;
import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.TokenRefreshResponse;
import rx.Observable;
import rx.Observer;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private CredentialDataSource credentialDataSource;

    public LoginDataSource(CredentialDataSource credentialDataSource) {
        this.credentialDataSource = credentialDataSource;
    }

    public Observable<LoginResponse> login(String username, String password, Context context) {

        HttpParams params = new HttpParams();
        //request parameters
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);
        params.putJsonParams(body.toString());

        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.LOGIN.getUrl())
                .httpMethod(RxVolley.Method.POST)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    LoginResponse response = new Gson().fromJson(responseJson, LoginResponse.class);
                    AuthRepository authRepository = AuthRepository.getInstance(new CredentialDataSource());
                    authRepository.setAccessToken(response.access);
                    authRepository.setRefreshToken(response.refresh, context);
                    credentialDataSource.setAccessToken(response.access);
                    credentialDataSource.saveRefreshToken(response.refresh, context);
                    return response;
                });
    }

    public Observable<TokenRefreshResponse> login(String refreshToken) {
        HttpParams params = new HttpParams();
        JsonObject body = new JsonObject();
        body.addProperty("refresh", refreshToken);
        params.putJsonParams(body.getAsString());

        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.LOGIN.getUrl())
                .httpMethod(RxVolley.Method.POST)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .getResult()
                .map(result -> {
                    String resultJson = new String(result.data);
                    TokenRefreshResponse loginResponse = new Gson().fromJson(resultJson, TokenRefreshResponse.class);
                    AuthRepository authRepository = AuthRepository.getInstance(new CredentialDataSource());
                    authRepository.setAccessToken(loginResponse.access);
                    return loginResponse;
                });
    }

    public void logout(String refreshToken) {
        /*
        JWT nie ma mechanizmu wylogowania jako tako. Ale wystarczy wymienic
        token na nowy i go nie zapamiętywać
         */
        this.login(refreshToken).first().subscribe(new Observer<TokenRefreshResponse>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onNext(TokenRefreshResponse tokenRefreshResponse) { }
        });
    }
}
