package pl.jhonylemon.dateapp.fragments.accountcreation;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import pl.jhonylemon.dateapp.fragments.BaseFragment;

import pl.jhonylemon.dateapp.utils.DataTransfer;
import pl.jhonylemon.dateapp.viewmodels.MainActivityViewModel;

public abstract class AccountCreationFragment extends BaseFragment {

    public static final String TAG="AccountCreation";

    private Boolean valid;

    public DataTransfer dataTransfer;

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    protected NavController getNavController() {
        return navController;
    }

    protected void setNavController(NavController navController) {
        this.navController = navController;
    }

    protected MainActivityViewModel getAuthenticationViewModel() {
        return mainActivityViewModel;
    }

    protected void moveToListSelection(){}

    protected abstract Task<DataSnapshot> load();
    protected abstract Task<Void> save();
    protected abstract void moveNext(View view);
    protected void movePrevious(View view){
        getActivity().onBackPressed();
    }
    protected abstract Boolean validate();
    protected abstract Boolean validateAndEnableNext();
    @Override
    protected View init(ViewBinding binding){
        super.init(binding);
        mainActivityViewModel.setActionBarVisible(true);
        mainActivityViewModel.setProgressBarVisible(true);
        dataTransfer = new DataTransfer();
        load();
        validate();
        return binding.getRoot();
    }
}
