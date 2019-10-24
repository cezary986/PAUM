package pl.polsl.workinghours.ui.user;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.data.user.UserRepository;
import pl.polsl.workinghours.ui.login.DataWrapper;

public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";

    private MutableLiveData<DataWrapper<User>> userData;
    private MutableLiveData<DataWrapper<String[]>> userGroupsData;

    private UserRepository userRepository;

    UserViewModel(Application context, UserRepository userRepository) {
        super(context);
        this.userRepository = userRepository;
    }

    public MutableLiveData<DataWrapper<User>> getProfile() {
        if (this.userData == null)
            this.userData = new MutableLiveData<>();
        this.userRepository.getUser(this.userData, getApplication());
        return userData;
    }

    public MutableLiveData<DataWrapper<String[]>> getUserGroups() {
        if (this.userGroupsData == null)
            this.userGroupsData = new MutableLiveData<>();
        this.userRepository.getUserGroups(this.userGroupsData, getApplication());
        return userGroupsData;
    }
}
