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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.RecyclerChatItemAdapter;
import pl.jhonylemon.dateapp.adapters.RecyclerChatWindowItemAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentChatBinding;
import pl.jhonylemon.dateapp.databinding.FragmentChatWindowBinding;
import pl.jhonylemon.dateapp.entity.UserChat;
import pl.jhonylemon.dateapp.entity.UserChatDetail;
import pl.jhonylemon.dateapp.entity.UserChatMessage;
import pl.jhonylemon.dateapp.entity.UserPairs;
import pl.jhonylemon.dateapp.fragments.main.MainFragment;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class ChatWindowFragment extends MainFragment {

    public static final String TAG="ChatFragment";
    private FragmentChatWindowBinding binding;
    private DataTransfer dataTransfer = new DataTransfer();
    private RecyclerChatWindowItemAdapter itemAdapter;
    private String uuid;

    public ChatWindowFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatWindowBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null){
            uuid=bundle.getString("UUID","");
        }

        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController=Navigation.findNavController(view);
        this.mainActivityViewModel.setBottomBarVisible(false);
        this.mainActivityViewModel.setActionBarVisible(true);

        if(uuid.isEmpty()){
            getActivity().onBackPressed();
        }

        itemAdapter = new RecyclerChatWindowItemAdapter(new ArrayList<>(), getContext());
        binding.chat.setAdapter(itemAdapter);
        binding.chat.setLayoutManager(new LinearLayoutManager(getContext()));

        dataTransfer.getUserChat(dataTransfer.getUUID(),uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserChatDetail chat = Optional.ofNullable(snapshot.getValue(UserChatDetail.class)).orElse(new UserChatDetail());
                itemAdapter.UpdateList(
                        chat.getMessages().values().stream()
                                .sorted(Comparator.comparingLong(UserChatMessage::getTimestamp))
                                .collect(Collectors.toList())
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(v->{
            if(!binding.editTextTextPersonName.getText().toString().isEmpty()){
                UserChatMessage userChatMessage = new UserChatMessage();
                userChatMessage.setTimestamp(System.currentTimeMillis());
                userChatMessage.setUuid(dataTransfer.getUUID());
                userChatMessage.setMessage(binding.editTextTextPersonName.getText().toString());
                dataTransfer.addUserChatMessage(dataTransfer.getUUID(),uuid,userChatMessage);
                binding.editTextTextPersonName.setText("");
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
        navController.navigate(id,null);
    }
}
