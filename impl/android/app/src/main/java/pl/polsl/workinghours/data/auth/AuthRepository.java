package pl.polsl.workinghours.data.auth;

import android.accounts.AuthenticatorException;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.RequestQueueProvider;
import pl.polsl.workinghours.data.login.LoginDataSource;
import pl.polsl.workinghours.data.login.LoginRepository;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.data.model.TokenRefreshResponse;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.network.JsonRequest;
import pl.polsl.workinghours.ui.login.DataWrapper;

public class AuthRepository {

    private static volatile AuthRepository instance;

    private RequestQueueProvider queueProvider;
    private CredentialDataSource dataSource;
    private JWT jwt;
    private String accessToken = null;
    private String refreshToken = null;

    // private constructor : singleton access
    private AuthRepository(CredentialDataSource dataSource, RequestQueueProvider queueProvider) {
        this.dataSource = dataSource;
        this.queueProvider = queueProvider;
    }

    public static AuthRepository getInstance(CredentialDataSource dataSource, RequestQueueProvider queueProvider) {
        if (instance == null) {
            instance = new AuthRepository(dataSource, queueProvider);
        }
        return instance;
    }

    public MutableLiveData<DataWrapper<String>> getAccessToken(Context context) throws AuthenticatorException {
        if (this.accessToken != null && !this.isAccessTokenExpired()) {
            MutableLiveData<DataWrapper<String>> result = new MutableLiveData<>();
            result.setValue(new DataWrapper<>(this.accessToken));
            return result;
        } else {
            // nie mamy tokena lub się przedawnił
            try {
                String refreshToken = this.dataSource.getRefreshToken(context);
                return this.obtainAccessToken(refreshToken);
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

    private MutableLiveData<DataWrapper<String>> obtainAccessToken(String refreshToken) {
        String url = Enviroment.Endpoints.LOGIN_REFRESH.getUrl();
        final int[] statusCode = new int[1];
        JSONObject body = new JSONObject();
        try {
            body.put("refresh", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MutableLiveData<DataWrapper<String>> result = new MutableLiveData<>();
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, body, response -> {
            TokenRefreshResponse tokenRefreshResponse = new Gson().fromJson(response.toString(), TokenRefreshResponse.class);
            this.accessToken = tokenRefreshResponse.access;
            result.postValue(new DataWrapper<>(tokenRefreshResponse.access));
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
        this.queueProvider.addToRequestQueue(jsonArrayRequest);
        return result;
    }

    public String getStoredAccessToken() {
        return this.accessToken;
    }

    private boolean isAccessTokenExpired() {
        JWT jwt = new JWT(this.accessToken);
        return jwt.isExpired(5);
    }
}
