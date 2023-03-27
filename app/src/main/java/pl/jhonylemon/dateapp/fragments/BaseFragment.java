package pl.jhonylemon.dateapp.fragments;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.viewbinding.ViewBinding;

import pl.jhonylemon.dateapp.viewmodels.MainActivityViewModel;

public abstract class BaseFragment extends Fragment {

    public static final String TAG="BaseFragment";

    protected NavController navController;
    protected MainActivityViewModel mainActivityViewModel;

    protected View init(ViewBinding binding){
        this.mainActivityViewModel=new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        return binding.getRoot();
    }

}
