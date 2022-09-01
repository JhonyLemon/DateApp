package pl.jhonylemon.dateapp.fragments.accountcreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.viewbinding.ViewBinding;

import pl.jhonylemon.dateapp.databinding.FragmentEnterNameBinding;
import pl.jhonylemon.dateapp.viewmodels.AuthenticationViewModel;

import lombok.Getter;
import lombok.Setter;

public abstract class AccountCreation extends Fragment {

    private Boolean valid;
    private NavController navController;
    private AuthenticationViewModel authenticationViewModel;

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

    protected AuthenticationViewModel getAuthenticationViewModel() {
        return authenticationViewModel;
    }

    protected void setAuthenticationViewModel(AuthenticationViewModel authenticationViewModel) {
        this.authenticationViewModel = authenticationViewModel;
    }

    protected void moveToListSelection(){}

    protected abstract void load();
    protected abstract void save();
    protected abstract void moveNext(View view);
    protected void movePrevious(View view){
        getActivity().onBackPressed();
    }
    protected abstract Boolean validate();
    protected View init(ViewBinding binding){
        this.authenticationViewModel=new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        load();
        validate();
        return binding.getRoot();
    }
}
