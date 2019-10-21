package pl.polsl.workinghours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import pl.polsl.workinghours.ui.login.DataWrapper;
import pl.polsl.workinghours.ui.user.UserViewModel;
import pl.polsl.workinghours.ui.user.UserViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    public static void startActivity(Activity currentActivity) {
        Intent myIntent = new Intent(currentActivity, MainActivity.class);
        currentActivity.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.userViewModel = new UserViewModelFactory(getApplication()).create(UserViewModel.class);

        this.fetchUserGroups();
    }

    private void fetchUserGroups() {
        this.userViewModel.getUserGroups().observe(this, dataWrapper -> {
            if (dataWrapper.isOk()) {
                dataWrapper.getSuccess();
                Toast.makeText(getApplicationContext(), new Gson().toJson(dataWrapper.getSuccess()), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to fetch user groups", Toast.LENGTH_LONG).show();
            }
        });
    }
}
