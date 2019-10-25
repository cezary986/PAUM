package pl.polsl.workinghours.data.user;
import com.google.gson.Gson;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;
import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.model.User;
import rx.Observable;

public class UserDataSource {

    public Observable<String[]> getUserGroups(String accessToken) {
        HttpParams params = new HttpParams();
        params.putHeaders("Authorization", "Bearer " + accessToken);
        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.GROUPS.getUrl())
                .httpMethod(RxVolley.Method.GET)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .shouldCache(false)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    return new Gson().fromJson(responseJson, String[].class);
                });
    }

    public Observable<User> getProfile(String accessToken) {
        HttpParams params = new HttpParams();
        params.putHeaders("Authorization", "Bearer " + accessToken);
        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.PROFILE.getUrl())
                .httpMethod(RxVolley.Method.GET)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .shouldCache(false)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    return new Gson().fromJson(responseJson, User.class);
                });
    }
}
