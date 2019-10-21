package pl.polsl.workinghours.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.data.user.UserRepository;
import pl.polsl.workinghours.ui.login.DataWrapper;

public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";

    private MutableLiveData<DataWrapper<User>> userData;
    private MutableLiveData<DataWrapper<String[]>> userGroupsData;

    private UserRepository userRepository;

    UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<DataWrapper<User>> getProfile() {
        if (this.userData == null)
            this.userData = new MutableLiveData<>();
        this.userRepository.getUser(this.userData);
        return userData;
    }

    public MutableLiveData<DataWrapper<String[]>> getUserGroups() {
        if (this.userGroupsData == null)
            this.userGroupsData = new MutableLiveData<>();
        this.userRepository.getUserGroups(this.userGroupsData);
        return userGroupsData;
    }
}
