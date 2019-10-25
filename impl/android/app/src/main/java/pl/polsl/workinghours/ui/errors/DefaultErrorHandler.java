package pl.polsl.workinghours.ui.errors;

import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.widget.Toast;

import com.kymjs.rxvolley.http.VolleyError;

import pl.polsl.workinghours.R;
import pl.polsl.workinghours.ui.login.LoginActivity;

public class DefaultErrorHandler implements IErrorHandler {

    private static DefaultErrorHandler instance;

    private DefaultErrorHandler() {
    }

    public static DefaultErrorHandler getInstance() {
        if (instance == null)
            instance = new DefaultErrorHandler();
        return instance;
    }

    @Override
    public void handleError(Throwable error, Activity activity) {
        if (error instanceof VolleyError) {
            this.handleApiError((VolleyError) error, activity);
        } else if (error instanceof AuthenticatorException) {
            this.handleAuthError(error, activity);
        } else {
            this.showToast(activity.getString(R.string.unknown_error), activity);
        }
    }

    private void handleApiError(VolleyError error, Activity activity) {
        switch (error.networkResponse.statusCode) {
            case 500:
                this.showToast(activity.getString(R.string.unknown_server_error_500), activity);
                break;
            case 401:
                this.handleAuthError(error, activity);
                break;
            case 403:
                this.showToast(activity.getString(R.string.server_error_403), activity);
                break;
            default:
                this.showToast(activity.getString(R.string.unknown_server_error_500), activity);
                break;
        }
    }

    private void handleAuthError(Throwable error, Activity activity) {
        // coś poszło ostro nie tak z autoryzacją - najprowdopodobniej nie ma tokenu
        // najbezpieczniej będzie wywołać ponowne logowanie użytkownika
        LoginActivity.startActivity(activity);
        activity.finish();
    }

    private void showToast(String text, Activity activity) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
}
