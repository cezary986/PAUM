package pl.polsl.workinghours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.data.model.WorkHours;
import pl.polsl.workinghours.data.model.WorkhoursListResponse;
import pl.polsl.workinghours.ui.work.WorkHoursModelFactory;
import pl.polsl.workinghours.ui.work.WorkHoursViewModel;
import rx.Observer;

public class EmployeeDataActivity extends AppCompatActivity {
    private WorkHoursViewModel workHoursViewModel;
    private ListView listView;
    CalendarView simpleCalendarView;
    WorkHours[] workHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);

        workHoursViewModel = ViewModelProviders.of(this, new WorkHoursModelFactory(getApplication()))
                .get(WorkHoursViewModel.class);

        getWorkHoursForSpecified((int) getIntent().getExtras().get("ID"));
        listView = findViewById(R.id.ViewWorkHoursEmployee);
        simpleCalendarView = (CalendarView) findViewById(R.id.calendarView);
        simpleCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // display the selected date by using a toast

            ArrayList<String> arrayList = new ArrayList<>();
            for (WorkHours w : workHours) {
                String date = WorkHours.extractDateFromDateTTime(w.started);
                String[] times = date.split("-");
                int y = Integer.parseInt(times[0]);
                int m = Integer.parseInt(times[1]);
                int d = Integer.parseInt(times[2]);

                if (y==year && m+1==month && d==dayOfMonth) {
                    if (w.finished == null) {
                        arrayList.add(date + "\n" + w.stringToTime(w.started) + "  -  " + "still working");
                    } else {
                        arrayList.add(date + "\n" + w.stringToTime(w.started) + "  -  " + w.stringToTime(w.finished));
                    }
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();

        });
    }

    public static void startActivity(Activity currentActivity, int id) {
        Intent myIntent = new Intent(currentActivity, EmployeeDataActivity.class);
        myIntent.putExtra("ID", id);
        currentActivity.startActivity(myIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getWorkHours(Integer.parseInt(getIntent().getStringExtra("ID")));
    }

    public void getWorkHoursForSpecified(int id) {
        workHoursViewModel.getWorkHoursForSpecified(id, this).first().subscribe(new Observer<WorkhoursListResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                // nie udało się trzeba się logować ręcznie
            }

            @Override
            public void onNext(WorkhoursListResponse workhoursListResponse) {
                workHours = workhoursListResponse.results;
                Arrays.sort(workHours);

                ArrayList<String> arrayList = new ArrayList<>();

                for (WorkHours w : workhoursListResponse.results) {
                    String date = WorkHours.extractDateFromDateTTime(w.started);
                    if (w.finished == null) {
                        arrayList.add(date + "\n" + w.stringToTime(w.started) + "  -  " + "still working");
                    } else {
                        arrayList.add(date + "\n" + w.stringToTime(w.started) + "  -  " + w.stringToTime(w.finished));
                    }
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}
