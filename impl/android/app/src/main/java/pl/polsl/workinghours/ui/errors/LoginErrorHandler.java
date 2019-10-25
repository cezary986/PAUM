package pl.polsl.workinghours.ui.errors;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.kymjs.rxvolley.http.VolleyError;

import pl.polsl.workinghours.R;

public class LoginErrorHandler implements IErrorHandler {

    private static LoginErrorHandler instance;

    private LoginErrorHandler() {
    }

    public static LoginErrorHandler getInstance() {
        if (instance == null)
            instance = new LoginErrorHandler();
        return instance;
    }

    @Override
    public void handleError(Throwable error, Activity activity) {
        if (error instanceof VolleyError) {
            this.handleApiError((VolleyError) error, activity);
        } else {
            DefaultErrorHandler.getInstance().handleError(error, activity);
        }
    }

    private void handleApiError(VolleyError error, Activity activity) {
        switch (error.networkResponse.statusCode) {
            case 401:
                showToast(activity.getString(R.string.login_error_401), activity);
                break;
            default:
                DefaultErrorHandler.getInstance().handleError(error, activity);
                break;
        }
    }

    private void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
