package pl.polsl.workinghours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pl.polsl.workinghours.data.model.QrCode;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.data.model.WorkHours;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import pl.polsl.workinghours.ui.errors.DefaultErrorHandler;
import pl.polsl.workinghours.ui.login.LoginActivity;
import pl.polsl.workinghours.ui.login.LoginViewModel;
import pl.polsl.workinghours.ui.login.LoginViewModelFactory;
import pl.polsl.workinghours.ui.user.UserViewModel;
import pl.polsl.workinghours.ui.user.UserViewModelFactory;
import pl.polsl.workinghours.ui.work.WorkHoursModelFactory;
import pl.polsl.workinghours.ui.work.WorkHoursViewModel;
import rx.Observer;

/**
 * Głowna aktywność dla pracownika
 */
public class MainEmployeeActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private LoginViewModel loginViewModel;
    private WorkHoursViewModel workHoursViewModel;

    private Button scanQrButton;
    private EditText employeeTextViewWorkHoursDate;
    private TextView employeeTextViewWorkHoursTitle;

    public static void startActivity(Activity currentActivity) {
        Intent myIntent = new Intent(currentActivity, MainEmployeeActivity.class);
        currentActivity.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employee);
        scanQrButton = findViewById(R.id.scanQrButton);
        employeeTextViewWorkHoursDate = findViewById(R.id.EmployeeTextViewWorkHoursDate);
        employeeTextViewWorkHoursTitle = findViewById(R.id.EmployeeTextViewWorkHoursTitle);

        userViewModel =  ViewModelProviders.of(this, new UserViewModelFactory(getApplication()))
                .get(UserViewModel.class);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);
        workHoursViewModel = ViewModelProviders.of(this, new WorkHoursModelFactory(getApplication()))
                .get(WorkHoursViewModel.class);

        this.fetchUserProfile();
       // getWorkHours(1572804140);


        scanQrButton.setOnClickListener(v -> {
            QrCodeScanActivity.startActivity(MainEmployeeActivity.this);
          // finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getWorkHours(1572739200);

    }

    public void getWorkHours(int date) {
        workHoursViewModel.getWorkHours(date,this).first().subscribe(new Observer<WorkhoursListResponse>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                // nie udało się trzeba się logować ręcznie
            }

            @Override
            public void onNext(WorkhoursListResponse workhoursListResponse) {

                WorkHours[] workHours = workhoursListResponse.results;
                if (workHours.length == 0){
                    employeeTextViewWorkHoursTitle.setText(Enviroment.START_WORK_DESC);
                } else {
                    WorkHours workHour = workHours[0];
                    if(workHour.finished == null ) {
                        employeeTextViewWorkHoursDate.setText(workHour.started);
                        employeeTextViewWorkHoursTitle.setText(Enviroment.WORK_DESC);
                    } else {
                        employeeTextViewWorkHoursDate.setText(workHour.finished);
                        employeeTextViewWorkHoursTitle.setText(Enviroment.END_WORK_DESC);
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle item selection */
        switch (item.getItemId()) {
            case R.id.logout:
                this.loginViewModel.logout(this);
                LoginActivity.startActivity(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchUserProfile() {
        // Używając first() nie trzeba wywoływać unsubscribe ale dostajesz wynik jedynie raz
        userViewModel.getProfile(this).first().subscribe(new Observer<User>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                DefaultErrorHandler.getInstance().handleError(e, MainEmployeeActivity.this);
            }

            @Override
            public void onNext(User user) {
                Toast.makeText(getApplication(), new Gson().toJson(user), Toast.LENGTH_LONG).show();
            }
        });
    }
}
