package pl.polsl.workinghours.ui.listemployee;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pl.polsl.workinghours.data.auth.AuthRepository;
import pl.polsl.workinghours.data.auth.CredentialDataSource;
import pl.polsl.workinghours.data.employee.EmployeeDataRepository;
import pl.polsl.workinghours.data.employee.EmployeeDataSource;
import pl.polsl.workinghours.data.work.WorkDataRepository;
import pl.polsl.workinghours.data.work.WorkDataSource;
import pl.polsl.workinghours.ui.work.WorkHoursViewModel;

/**
 * ViewModel provider factory to instantiate QrCodeViewModel.
 * Required given QrCodeViewModel has a non-empty constructor
 */
public class ListEmployeeModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application
     */
    public ListEmployeeModelFactory(@NonNull Application application) {
        super(application);

        this.context = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListEmployeeViewModel.class)) {
            return (T) new ListEmployeeViewModel(
                    this.context,
                    EmployeeDataRepository.getInstance(
                            AuthRepository.getInstance(new CredentialDataSource()),
                            new EmployeeDataSource()
                    ));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
