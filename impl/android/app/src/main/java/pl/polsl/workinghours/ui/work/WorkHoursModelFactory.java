package pl.polsl.workinghours.ui.work;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.qrcode.QrCodeDataSource;
import pl.polsl.workinghours.data.qrcode.QrCodeRepository;
import pl.polsl.workinghours.data.work.WorkDataRepository;
import pl.polsl.workinghours.data.work.WorkDataSource;
import pl.polsl.workinghours.ui.qrcode.QrCodeViewModel;

/**
 * ViewModel provider factory to instantiate QrCodeViewModel.
 * Required given QrCodeViewModel has a non-empty constructor
 */
public class WorkHoursModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application
     */
    public WorkHoursModelFactory(@NonNull Application application) {
        super(application);

        this.context = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WorkHoursViewModel.class)) {
            return (T) new WorkHoursViewModel(
                    this.context,
                    WorkDataRepository.getInstance(
                            AuthRepository.getInstance(new CredentialDataSource()),
                            new WorkDataSource()
                    ));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
