package pl.polsl.workinghours.ui.user;
import android.app.Application;
import android.content.Context;
import androidx.lifecycle.AndroidViewModel;
import pl.polsl.workinghours.data.model.User;
import pl.polsl.workinghours.data.user.UserRepository;
import rx.Observable;

public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "QrCodeViewModel";

    private UserRepository userRepository;

    UserViewModel(Application context, UserRepository userRepository) {
        super(context);
        this.userRepository = userRepository;
    }

    public Observable<User> getProfile(Context context) {
        return this.userRepository.getUser(context);
    }

    public Observable<String[]> getUserGroups(Context context) {
        return this.userRepository.getUserGroups(context);
    }
}
