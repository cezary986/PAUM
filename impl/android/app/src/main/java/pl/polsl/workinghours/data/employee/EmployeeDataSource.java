package pl.polsl.workinghours.data.employee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.model.EmployeeListResponse;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import rx.Observable;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class EmployeeDataSource {

    public EmployeeDataSource() {
    }

    public Observable<EmployeeListResponse> getEmployyeListResponse(String accessToken) {

        HttpParams params = new HttpParams();
        JsonObject body = new JsonObject();
        params.putHeaders("Authorization", "Bearer " + accessToken);
        params.putJsonParams(body.toString());


        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.EMPLOYEES_LIST.getUrl())
                .httpMethod(RxVolley.Method.GET)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .shouldCache(false)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    EmployeeListResponse response = new Gson().fromJson(responseJson, EmployeeListResponse.class);
                    return response;
                });
    }
}
