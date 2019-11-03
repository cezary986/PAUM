package pl.polsl.workinghours.ui.qrcode;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.qrcode.QrCodeDataSource;
import pl.polsl.workinghours.data.qrcode.QrCodeRepository;

/**
 * ViewModel provider factory to instantiate QrCodeViewModel.
 * Required given QrCodeViewModel has a non-empty constructor
 */
public class QrCodeModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application
     */
    public QrCodeModelFactory(@NonNull Application application) {
        super(application);

        this.context = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QrCodeViewModel.class)) {
            return (T) new QrCodeViewModel(
                    this.context,
                    QrCodeRepository.getInstance(
                            AuthRepository.getInstance(new CredentialDataSource()),
                            new QrCodeDataSource()
                    ));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
