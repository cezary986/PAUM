package pl.polsl.workinghours.ui.qrcode;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import pl.polsl.workinghours.data.model.QrCode;
import pl.polsl.workinghours.data.qrcode.QrCodeRepository;
import rx.Observable;

public class QrCodeViewModel extends AndroidViewModel {

    private static final String TAG = "QrCodeViewModel";

    private QrCodeRepository qrCodeRepository;

    QrCodeViewModel(Application context, QrCodeRepository qrCodeRepository) {
        super(context);
        this.qrCodeRepository = qrCodeRepository;
    }

    public Observable<QrCode> postQrCode(String code, Context context) {
        return this.qrCodeRepository.sendQrCode(code, context);
    }

}
