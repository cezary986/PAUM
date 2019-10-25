package pl.polsl.workinghours.data.user;
import android.accounts.AuthenticatorException;
import android.content.Context;
import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.model.User;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class UserRepository {

    private static volatile UserRepository instance;

    private UserDataSource userDataSource;
    private AuthRepository authRepository;

    private UserRepository(
            UserDataSource userDataSource,
            AuthRepository authRepository) {
        this.userDataSource = userDataSource;
        this.authRepository = authRepository;
    }

    public static UserRepository getInstance(UserDataSource userDataSource, AuthRepository authRepository) {
        if (instance == null) {
            instance = new UserRepository(userDataSource, authRepository);
        }
        return instance;
    }

    public Observable<String[]> getUserGroups(Context context) {
        try {
            return this.authRepository.getAccessToken(context)
                    .first()
                    .flatMap(accessToken -> {
                        return userDataSource.getUserGroups(accessToken);
                    });
        } catch (AuthenticatorException e) {
            BehaviorSubject<String[]> result = BehaviorSubject.create();
            BehaviorSubject.create().onError(e);
            e.printStackTrace();
            return result;
        }
    }

    public Observable<User> getUser(Context context) {
        try {
            return this.authRepository.getAccessToken(context)
                    .first()
                    .flatMap(accessToken -> {
                        return userDataSource.getProfile(accessToken);
                    });
        } catch (AuthenticatorException e) {
            BehaviorSubject<User> result = BehaviorSubject.create();
            BehaviorSubject.create().onError(e);
            e.printStackTrace();
            return result;
        }
    }
}
