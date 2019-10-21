package pl.polsl.workinghours.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singletone przechowujący RequestQueue. To dość duży obiekt i lepiej żeby istniał tylko jeden
 * w całej appce
 */
public class RequestQueueProvider {
    private static RequestQueueProvider instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private RequestQueueProvider(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueProvider getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueProvider(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
