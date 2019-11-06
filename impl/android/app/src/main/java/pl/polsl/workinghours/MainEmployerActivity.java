package pl.polsl.workinghours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.ui.errors.DefaultErrorHandler;
import pl.polsl.workinghours.ui.login.LoginActivity;
import pl.polsl.workinghours.ui.login.LoginViewModel;
import pl.polsl.workinghours.ui.login.LoginViewModelFactory;
import pl.polsl.workinghours.ui.user.UserViewModel;
import pl.polsl.workinghours.ui.user.UserViewModelFactory;
import rx.Observer;

/**
 * Głowna aktywność dla pracodawcy
 */
public class MainEmployerActivity extends AppCompatActivity {

    private static final String USER_GROUPS_EXTRA_KEY = "GROUPS";

    private UserViewModel userViewModel;
    private LoginViewModel loginViewModel;
    /** Nazwy wszystkich grup do jakich należy użytkownik */
    private String[] userGroups;

    /**
     * @param currentActivity
     * @param groups grupy użykownika, przekazywane na wypadek gdyby był i pracownikiem i pracodawcą
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employer);


        this.getIntentExtraData();

        userViewModel =  ViewModelProviders.of(this, new UserViewModelFactory(getApplication()))
                .get(UserViewModel.class);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);

        this.fetchUserProfile();

        TextView textView = findViewById(R.id.textView2);
        for (String groupName : userGroups) {
            if (groupName.equals(Enviroment.Groups.EMPLOYEE))
                textView.setText(textView.getText() + " i pracownik");
        }
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
                DefaultErrorHandler.getInstance().handleError(e, MainEmployerActivity.this);
            }

            @Override
            public void onNext(User user) {
                Toast.makeText(getApplication(), new Gson().toJson(user), Toast.LENGTH_LONG).show();
            }
        });
    }
}
