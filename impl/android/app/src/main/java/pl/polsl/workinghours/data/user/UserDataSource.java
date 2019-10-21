package pl.polsl.workinghours.data.user;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.RequestQueueProvider;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.network.JsonRequest;
import pl.polsl.workinghours.ui.login.DataWrapper;

public class UserDataSource {

    private RequestQueueProvider requestQueueProvider;

    public UserDataSource(RequestQueueProvider requestQueueProvider
    ) {
        this.requestQueueProvider = requestQueueProvider;
    }

    public void getUserGroups(String accessToken, MutableLiveData<DataWrapper<String[]>> result) {
        String url = Enviroment.Endpoints.GROUPS.getUrl();
        final int[] statusCode = new int[1];

        JsonRequest request = new JsonRequest(
                Request.Method.GET,
                url,
                null,
                accessToken,
                response -> {
                    String[] groups = new Gson().fromJson(response, String[].class);
                    result.postValue(new DataWrapper<>(groups));
                }, volleyerror -> {
            switch (statusCode[0]) {
                case 500:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.SERVER_ERROR_500));
                    break;
                case 401:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.UNATHORIZED));
                    break;
                default:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.UNDEFINED_ERROR));
                    break;
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        this.requestQueueProvider.addToRequestQueue(request);
    }

    public void getProfile(String accessToken, MutableLiveData<DataWrapper<User>> result) {
        String url = Enviroment.Endpoints.GROUPS.getUrl();
        final int[] statusCode = new int[1];

        JsonRequest request = new JsonRequest(
                Request.Method.GET,
                url,
                null,
                accessToken,
                response -> {
                    User profile = new Gson().fromJson(response, User.class);
                    result.postValue(new DataWrapper<>(profile));
                }, volleyerror -> {
            switch (statusCode[0]) {
                case 500:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.SERVER_ERROR_500));
                    break;
                case 401:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.UNATHORIZED));
                    break;
                default:
                    result.postValue(new DataWrapper<>(DataWrapper.ErrorCodes.UNDEFINED_ERROR));
                    break;
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        this.requestQueueProvider.addToRequestQueue(request);
    }
}
