package pl.polsl.workinghours.network;
import androidx.annotation.Nullable;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonRequest extends StringRequest {

    private String bodyString;
    private String accessToken;

    public JsonRequest(
            int method,
            String url,
            Object body,
            String accessCode,
            Response.Listener<String> listener,
            @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.bodyString = new Gson().toJson(body);
        this.accessToken = accessCode;
    }

    public JsonRequest(
            int method,
            String url,
            Object body,
            Response.Listener<String> listener,
            @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.bodyString = new Gson().toJson(body);
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return this.bodyString == null ? null : this.bodyString.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        return Response.success(new String(response.data), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String>  params = new HashMap<>();
        params.put("Authorization", "Bearer " + this.accessToken);
        return params;
    }
}