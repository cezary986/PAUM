package pl.polsl.workinghours.data.login;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.RequestQueueProvider;
import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.ui.login.DataWrapper;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private RequestQueueProvider requestQueueProvider;
    private CredentialDataSource credentialDataSource;

    public LoginDataSource(
            RequestQueueProvider requestQueueProvider,
            CredentialDataSource credentialDataSource) {
        this.requestQueueProvider = requestQueueProvider;
        this.credentialDataSource = credentialDataSource;
    }

    public void login(
            String username,
            String password,
            Context context,
            MutableLiveData<DataWrapper<LoginResponse>> result) throws JSONException {

        String url = Enviroment.Endpoints.LOGIN.getUrl();
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        final int[] statusCode = new int[1];
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, response -> {
            LoginResponse loginResponse = new Gson().fromJson(response.toString(), LoginResponse.class);
            AuthRepository authRepository = AuthRepository.getInstance(new CredentialDataSource(), requestQueueProvider);
            authRepository.setAccessToken(loginResponse.access);
            authRepository.setRefreshToken(loginResponse.refresh, context);
            this.credentialDataSource.setAccessToken(loginResponse.access);
            this.credentialDataSource.saveRefreshToken(loginResponse.refresh, context);
            result.postValue(new DataWrapper<>(loginResponse));
        }, volleyerror -> {
            switch (statusCode[0]) {
                case 500:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.SERVER_ERROR_500));
                    break;
                case 401:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.WRONG_CREDENTIALS));
                    break;
                default:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.UNDEFINED_ERROR));
                    break;
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        this.requestQueueProvider.addToRequestQueue(request);
    }

    public void login(
            String refreshToken,
            MutableLiveData<DataWrapper<LoginResponse>> result
    ) throws JSONException {
        String url = Enviroment.Endpoints.LOGIN.getUrl();
        JSONObject body = new JSONObject();
        body.put("refresh", refreshToken);
        final int[] statusCode = new int[1];
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, response -> {
            LoginResponse loginResponse = new Gson().fromJson(response.toString(), LoginResponse.class);
            AuthRepository authRepository = AuthRepository.getInstance(new CredentialDataSource(), requestQueueProvider);
            authRepository.setAccessToken(loginResponse.access);
            loginResponse.refresh = refreshToken;
            this.credentialDataSource.setAccessToken(loginResponse.access);
            result.postValue(new DataWrapper<>(loginResponse));
        }, volleyerror -> {
            switch (statusCode[0]) {
                case 500:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.SERVER_ERROR_500));
                    break;
                case 401:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.WRONG_CREDENTIALS));
                    break;
                default:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.UNDEFINED_ERROR));
                    break;
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        this.requestQueueProvider.addToRequestQueue(request);
    }

    public void logout(String refreshToken) {
        /*
        JWT nie ma mechanizmu wylogowania jako tako. Ale wystarczy wymienic
        token na nowy i go nie zapamiętywać
         */
        try {
            this.login(refreshToken, new MutableLiveData<>());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
