package pl.polsl.workinghours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.ui.errors.DefaultErrorHandler;
import pl.polsl.workinghours.ui.user.UserViewModel;
import pl.polsl.workinghours.ui.user.UserViewModelFactory;
import rx.Observer;

/**
 * Głowna aktywność dla pracodawcy
 */
public class MainEmployerActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    public static void startActivity(Activity currentActivity) {
        Intent myIntent = new Intent(currentActivity, MainEmployerActivity.class);
        currentActivity.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employer);

        userViewModel =  ViewModelProviders.of(this, new UserViewModelFactory(getApplication()))
                .get(UserViewModel.class);

        this.fetchUserProfile();
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
