package pl.polsl.workinghours.data.work;

import android.accounts.AuthenticatorException;
import android.content.Context;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.model.QrCode;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import pl.polsl.workinghours.data.qrcode.QrCodeDataSource;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class WorkDataRepository {

    private static volatile WorkDataRepository instance;
    private WorkDataSource workDataSource;
    private AuthRepository authRepository;

    // private constructor : singleton access
    private WorkDataRepository(AuthRepository authRepository,  WorkDataSource workDataSource) {
        this.workDataSource = workDataSource;
        this.authRepository = authRepository;
    }

    public static WorkDataRepository getInstance(
            AuthRepository authRepository,
            WorkDataSource workDataSource) {
        if (instance == null) {
            instance = new WorkDataRepository(authRepository, workDataSource);
        }
        return instance;
    }

    public Observable<WorkhoursListResponse> getWorkHours(Integer date, Context context) {
        try {
            return this.authRepository.getAccessToken(context)
                    .first()
                    .flatMap(accessToken -> workDataSource.getWorkHours(date, accessToken));
        } catch (AuthenticatorException e) {
            BehaviorSubject<WorkhoursListResponse> result = BehaviorSubject.create();
            BehaviorSubject.create().onError(e);
            e.printStackTrace();
            return result;
        }
    }


}
