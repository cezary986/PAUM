package pl.polsl.workinghours.ui.errors;

import android.app.Activity;

public interface IErrorHandler {

    void handleError(Throwable error, Activity activity);
}
