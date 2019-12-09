package pl.polsl.workinghours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import java.util.ArrayList;
import java.util.Locale;
import com.google.gson.Gson;

import pl.polsl.workinghours.data.model.EmployeeListResponse;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.ui.errors.DefaultErrorHandler;
import pl.polsl.workinghours.ui.listemployee.ListEmployeeModelFactory;
import pl.polsl.workinghours.ui.listemployee.ListEmployeeViewModel;
import pl.polsl.workinghours.ui.login.LoginActivity;
import pl.polsl.workinghours.ui.login.LoginViewModel;
import pl.polsl.workinghours.ui.login.LoginViewModelFactory;
import pl.polsl.workinghours.ui.user.UserViewModel;
import pl.polsl.workinghours.ui.user.UserViewModelFactory;
import rx.Observer;

/**
 * Głowna aktywność dla pracodawcy
 */
public class MainEmployerActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String USER_GROUPS_EXTRA_KEY = "GROUPS";

    private UserViewModel userViewModel;
    private LoginViewModel loginViewModel;
    private ListEmployeeViewModel listEmployeeViewModel;
    ArrayList<String> arrayList;

    SearchView editsearch;
    Context context;
    /**
     * Nazwy wszystkich grup do jakich należy użytkownik
     */
    private String[] userGroups;
    private ListView listView;

    /**
     * @param currentActivity
     * @param groups          grupy użykownika, przekazywane na wypadek gdyby był i pracownikiem i pracodawcą
     */
    public static void startActivity(Activity currentActivity, String[] groups) {
        Intent myIntent = new Intent(currentActivity, MainEmployerActivity.class);
        myIntent.putExtra(MainEmployerActivity.USER_GROUPS_EXTRA_KEY, groups);
        currentActivity.startActivity(myIntent);
    }

    private void getIntentExtraData() {
        Intent intent = this.getIntent();
        this.userGroups = intent.getStringArrayExtra(MainEmployerActivity.USER_GROUPS_EXTRA_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employer);
        listView = findViewById(R.id.ViewListEmployee);

        this.getIntentExtraData();

        userViewModel = ViewModelProviders.of(this, new UserViewModelFactory(getApplication()))
                .get(UserViewModel.class);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);
        listEmployeeViewModel = ViewModelProviders.of(this, new ListEmployeeModelFactory(getApplication()))
                .get(ListEmployeeViewModel.class);


        this.fetchUserProfile();
        getEmployeeList();

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String o = (String) listView.getItemAtPosition(position);
                int id = Integer.parseInt(o.split("ID:")[1].trim());
                EmployeeDataActivity.startActivity(MainEmployerActivity.this, id);
            }
        });

        TextView textView = findViewById(R.id.textView2);
        for (String groupName : userGroups) {
            if (groupName.equals(Enviroment.Groups.EMPLOYEE))
                textView.setText(textView.getText() + " i pracownik");
        }

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }


    public void getEmployeeList() {
        listEmployeeViewModel.getEmployeeList(this).first().subscribe(new Observer<EmployeeListResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                // nie udało się trzeba się logować ręcznie
            }

            @Override
            public void onNext(EmployeeListResponse employeeListResponse) {

                arrayList = new ArrayList<>();
                for (User u : employeeListResponse.results) {
                    arrayList.add("NAME: " + u.username + "\nID: " + u.id);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();

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
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                DefaultErrorHandler.getInstance().handleError(e, MainEmployerActivity.this);
            }

            @Override
            public void onNext(User user) {
                Toast.makeText(getApplication(), new Gson().toJson(user), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        ArrayList animalNamesList = new ArrayList();
        animalNamesList.clear();
        if (charText.length() == 0) {
            animalNamesList.addAll(arrayList);
        } else {
            for (String wp : arrayList) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    animalNamesList.add(wp);
                }
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, animalNamesList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
       // adapter.filter(text);
        return false;
    }
}

