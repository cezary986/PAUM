package pl.polsl.workinghours.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class DataWrapper<T> {
    @Nullable
    private T success;
    @Nullable
    private Integer error;

    public DataWrapper(@Nullable Integer error) {
        this.error = error;
    }

    public DataWrapper(@Nullable T success) {
        this.success = success;
    }

    @Nullable
    public T getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }

    public boolean isOk() {
        return this.error == null && this.success != null;
    }

    public interface ErrorCodes {
        int UNDEFINED_ERROR = -1;
        int WRONG_CREDENTIALS = 0;
        int SERVER_ERROR_500 = 1;
        int UNATHORIZED = 2;
    }
}
