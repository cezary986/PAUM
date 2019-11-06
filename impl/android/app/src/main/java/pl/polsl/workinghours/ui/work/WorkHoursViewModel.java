package pl.polsl.workinghours.ui.work;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import pl.polsl.workinghours.data.model.QrCode;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import pl.polsl.workinghours.data.qrcode.QrCodeRepository;
import pl.polsl.workinghours.data.work.WorkDataRepository;
import rx.Observable;

public class WorkHoursViewModel extends AndroidViewModel {

    private static final String TAG = "WorkHoursViewModel";

    private WorkDataRepository workDataRepository;

    WorkHoursViewModel(Application context, WorkDataRepository workDataRepository) {
        super(context);
        this.workDataRepository = workDataRepository;
    }

    public Observable<WorkhoursListResponse> getWorkHours(long date, Context context) {
        return this.workDataRepository.getWorkHours(date, context);
    }

}
