package pl.jhonylemon.dateapp.fragments.main.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.RecyclerChatItemAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentChatBinding;
import pl.jhonylemon.dateapp.entity.UserPair;
import pl.jhonylemon.dateapp.entity.UserPairs;
import pl.jhonylemon.dateapp.fragments.BaseFragment;
import pl.jhonylemon.dateapp.fragments.main.MainFragment;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class ChatFragment extends MainFragment {

    public static final String TAG="ChatFragment";
    private FragmentChatBinding binding;
    private NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.chatFragment,true).build();
    private DataTransfer dataTransfer = new DataTransfer();
    private RecyclerChatItemAdapter itemAdapter;

    public ChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController=Navigation.findNavController(view);
        this.mainActivityViewModel.setBottomBarVisible(true);
        this.mainActivityViewModel.setActionBarVisible(true);

        itemAdapter = new RecyclerChatItemAdapter(new ArrayList<>(), getContext(), new RecyclerChatItemAdapter.OnRecyclerItemClick() {
            @Override
            public void onItemClick(String uuid, Integer position) {
                Bundle bundle = new Bundle();
                bundle.putString("UUID",uuid);
                navController.navigate(R.id.chatWindowFragment,bundle);
                //move to chat
            }

            @Override
            public void onButtonItemClick(String uuid, Integer position) {
                dataTransfer.setPairVote(dataTransfer.getUUID(),uuid,false);
            }
        });
        binding.chats.setAdapter(itemAdapter);
        binding.chats.setLayoutManager(new LinearLayoutManager(getContext()));

        dataTransfer.getUserPairs(dataTransfer.getUUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserPairs pairs = Optional.ofNullable(snapshot.getValue(UserPairs.class)).orElse(new UserPairs());
                List<String> uuids = new ArrayList<>();
                Map<String, UserPair> pair = Optional.ofNullable(pairs.getPairs()).orElse(new HashMap<>());
                pair.forEach((k,v)->{
                    if(Optional.ofNullable(v.getMyVote()).orElse(false) && Optional.ofNullable(v.getTheirVote()).orElse(false)){
                        uuids.add(k);
                    }
                });
                itemAdapter.UpdateList(uuids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void move(int id) {
        navController.navigate(id,null,navOptions);
    }
}
