package pl.polsl.workinghours.data.work;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.model.QrCode;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import rx.Observable;


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class WorkDataSource {

    public WorkDataSource() {
    }

    public Observable<WorkhoursListResponse> getWorkHours(long date, String accessToken) {

        HttpParams params = new HttpParams();
        JsonObject body = new JsonObject();
        Long dateLong = date;
        params.put("date", dateLong.toString() );
        params.putHeaders("Authorization", "Bearer " + accessToken);
        params.putJsonParams(body.toString());


        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.EMPLOYEES_WORK_HOURS_MINE.getUrl())
                .httpMethod(RxVolley.Method.GET)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .shouldCache(false)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    WorkhoursListResponse response = new Gson().fromJson(responseJson, WorkhoursListResponse.class);
                    return response;
                });
    }

    public Observable<WorkhoursListResponse> getWorkHoursForSpecified(int id, String accessToken) {

        HttpParams params = new HttpParams();
        JsonObject body = new JsonObject();
        params.putHeaders("Authorization", "Bearer " + accessToken);
        params.putJsonParams(body.toString());

        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.EMPLOYEES_LIST.getUrl()+id+"/work_hours/")
                .httpMethod(RxVolley.Method.GET)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .shouldCache(false)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    WorkhoursListResponse response = new Gson().fromJson(responseJson, WorkhoursListResponse.class);
                    return response;
                });
    }

}
