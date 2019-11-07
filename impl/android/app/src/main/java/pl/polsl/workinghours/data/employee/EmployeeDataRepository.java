package pl.polsl.workinghours.data.employee;

import android.accounts.AuthenticatorException;
import android.content.Context;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.model.EmployeeListResponse;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import pl.polsl.workinghours.data.work.WorkDataSource;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class EmployeeDataRepository {

    private static volatile EmployeeDataRepository instance;
    private EmployeeDataSource employeeDataSource;
    private AuthRepository authRepository;

    // private constructor : singleton access
    private EmployeeDataRepository(AuthRepository authRepository, EmployeeDataSource employeeDataSource) {
        this.employeeDataSource = employeeDataSource;
        this.authRepository = authRepository;
    }

    public static EmployeeDataRepository getInstance(
            AuthRepository authRepository,
            EmployeeDataSource employeeDataSource) {
        if (instance == null) {
            instance = new EmployeeDataRepository(authRepository, employeeDataSource);
        }
        return instance;
    }

    public Observable<EmployeeListResponse> getEmployeeList(Context context) {
        try {
            return this.authRepository.getAccessToken(context)
                    .first()
                    .flatMap(accessToken -> employeeDataSource.getEmployyeListResponse(accessToken));
        } catch (AuthenticatorException e) {
            BehaviorSubject<EmployeeListResponse> result = BehaviorSubject.create();
            BehaviorSubject.create().onError(e);
            e.printStackTrace();
            return result;
        }
    }


}
