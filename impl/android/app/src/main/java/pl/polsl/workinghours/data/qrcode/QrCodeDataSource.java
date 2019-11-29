package pl.polsl.workinghours.data.qrcode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpParams;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.data.model.QrCode;
import rx.Observable;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class QrCodeDataSource {

    public QrCodeDataSource() {
    }

    public Observable<QrCode> postQrCode(String code, String accessToken) {

        HttpParams params = new HttpParams();
        //request parameters
        JsonObject body = new JsonObject();
        body.addProperty("code", code);
        params.putHeaders("Authorization", "Bearer " + accessToken);
        params.putJsonParams(body.toString());

        return new RxVolley.Builder()
                .url(Enviroment.Endpoints.CODE.getUrl())
                .httpMethod(RxVolley.Method.POST)
                .cacheTime(Enviroment.REQUEST_CACHE_TIME)
                .contentType(RxVolley.ContentType.JSON)
                .params(params)
                .getResult()
                .map(result -> {
                    String responseJson = new String(result.data);
                    QrCode response = new Gson().fromJson(responseJson, QrCode.class);
                    return response;
                });
    }
}
