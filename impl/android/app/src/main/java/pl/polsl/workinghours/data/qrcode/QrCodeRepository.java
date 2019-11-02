package pl.polsl.workinghours.data.qrcode;

import android.accounts.AuthenticatorException;
import android.content.Context;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.model.QrCode;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class QrCodeRepository {

    private static volatile QrCodeRepository instance;
    private QrCodeDataSource qrCodeDataSource;
    private AuthRepository authRepository;

    // private constructor : singleton access
    private QrCodeRepository(AuthRepository authRepository, QrCodeDataSource qrCodeDataSource) {
        this.qrCodeDataSource = qrCodeDataSource;
        this.authRepository = authRepository;
    }

    public static QrCodeRepository getInstance(
            AuthRepository authRepository,
            QrCodeDataSource qrCodeDataSource) {
        if (instance == null) {
            instance = new QrCodeRepository(authRepository, qrCodeDataSource);
        }
        return instance;
    }

    public Observable<QrCode> sendQrCode(String code, Context context) {
        try {
            return this.authRepository.getAccessToken(context)
                    .first()
                    .flatMap(accessToken -> qrCodeDataSource.postQrCode(code, accessToken));
        } catch (AuthenticatorException e) {
            BehaviorSubject<QrCode> result = BehaviorSubject.create();
            BehaviorSubject.create().onError(e);
            e.printStackTrace();
            return result;
        }
    }


}
