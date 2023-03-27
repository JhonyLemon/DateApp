package pl.jhonylemon.dateapp.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthChecker {

    private final FirebaseAuth mAuth;
    public AuthChecker() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public Boolean checkUser(){
        if(mAuth.getCurrentUser()==null){
            return false;
        }
        return true;
    }

    public Task<FirebaseUser> reloadUser(){
        if(this.mAuth.getCurrentUser()==null){
            Tasks.forCanceled();
        }
           return this.mAuth
                    .getCurrentUser()
                    .reload()
                    .continueWith(task -> mAuth.getCurrentUser());
    }
}
