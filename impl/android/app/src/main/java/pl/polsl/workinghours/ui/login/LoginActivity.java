package pl.polsl.workinghours.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import pl.polsl.workinghours.Enviroment;
import pl.polsl.workinghours.MainEmployeeActivity;
import pl.polsl.workinghours.MainEmployerActivity;
import pl.polsl.workinghours.R;
import pl.polsl.workinghours.data.model.LoginResponse;
import pl.polsl.workinghours.ui.errors.DefaultErrorHandler;
import pl.polsl.workinghours.ui.errors.LoginErrorHandler;
import pl.polsl.workinghours.ui.user.UserViewModel;
import pl.polsl.workinghours.ui.user.UserViewModelFactory;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class LoginActivity extends AppCompatActivity {

    private BehaviorSubject<Object> unsubscribe = BehaviorSubject.create();
    private LoginViewModel loginViewModel;
    private UserViewModel userViewModel;

    private ProgressBar loadingProgressBar;

    public static void startActivity(Activity currentActivity) {
        Intent myIntent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(myIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);
        userViewModel =  ViewModelProviders.of(this, new UserViewModelFactory(getApplication()))
                .get(UserViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        this.loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState()
                .takeUntil(unsubscribe)
                .subscribe(new Observer<LoginFormState>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        DefaultErrorHandler.getInstance().handleError(e, LoginActivity.this);
                    }

                    @Override
                    public void onNext(LoginFormState loginFormState) {
                        if (loginFormState == null) {
                            return;
                        }
                        loginButton.setEnabled(loginFormState.isDataValid());
                        if (loginFormState.getUsernameError() != null) {
                            usernameEditText.setError(getString(loginFormState.getUsernameError()));
                        }
                        if (loginFormState.getPasswordError() != null) {
                            passwordEditText.setError(getString(loginFormState.getPasswordError()));
                        }
                    }
                });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    getApplicationContext()
            ).takeUntil(unsubscribe).subscribe(new Observer<LoginResponse>() {
                @Override
                public void onCompleted() { }

                @Override
                public void onError(Throwable e) {
                    loadingProgressBar.setVisibility(View.GONE);
                    LoginErrorHandler.getInstance().handleError(e, LoginActivity.this);
                }

                @Override
                public void onNext(LoginResponse loginResponse) {
                    onLoginSuccess();
                }
            });
        });
    }

    private void onLoginSuccess() {
        this.userViewModel.getUserGroups(this).first().subscribe(new Observer<String[]>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) {
                DefaultErrorHandler.getInstance().handleError(e, LoginActivity.this);
            }

            @Override
            public void onNext(String[] userGroups) {
                for (String group : userGroups) {
                    if (group.equals(Enviroment.Groups.EMPLOYER)) {
                        MainEmployerActivity.startActivity(LoginActivity.this);
                        finish();
                        break;
                    }
                    if (group.equals(Enviroment.Groups.EMPLOYEE)) {
                        MainEmployeeActivity.startActivity(LoginActivity.this);
                        finish();
                        break;
                    }
                }
                loadingProgressBar.setVisibility(View.GONE);
                setResult(Activity.RESULT_OK);
                unsubscribe.onCompleted();
            }
        });
    }
}
