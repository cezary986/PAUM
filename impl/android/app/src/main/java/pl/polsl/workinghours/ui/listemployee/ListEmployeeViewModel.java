package pl.polsl.workinghours.ui.listemployee;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import pl.polsl.workinghours.data.employee.EmployeeDataRepository;
import pl.polsl.workinghours.data.model.EmployeeListResponse;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import pl.polsl.workinghours.data.work.WorkDataRepository;
import rx.Observable;

public class ListEmployeeViewModel extends AndroidViewModel {

    private static final String TAG = "WorkHoursViewModel";

    private EmployeeDataRepository employeeDataRepository;

    ListEmployeeViewModel(Application context, EmployeeDataRepository employeeDataRepository) {
        super(context);
        this.employeeDataRepository = employeeDataRepository;
    }

    public Observable<EmployeeListResponse> getEmployeeList(Context context) {
        return this.employeeDataRepository.getEmployeeList(context);
    }

}
