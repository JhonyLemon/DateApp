package pl.jhonylemon.dateapp.fragments.main.implementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.adapters.CardStackViewAdapter;
import pl.jhonylemon.dateapp.databinding.FragmentSwipeBinding;
import pl.jhonylemon.dateapp.fragments.main.MainFragment;
import pl.jhonylemon.dateapp.models.StackCardCard;
import pl.jhonylemon.dateapp.entity.UserData;
import pl.jhonylemon.dateapp.entity.UserPair;
import pl.jhonylemon.dateapp.entity.UserPairs;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class SwipeFragment extends MainFragment {

    public static final String TAG="SwipeFragment";
    private FragmentSwipeBinding binding;
    private NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.swipeFragment,true).build();
    private CardStackViewAdapter cardStackViewAdapter;
    CardStackLayoutManager manager;
    private DataTransfer dataTransfer;
    private List<String> uuids = new ArrayList<>();

    public SwipeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSwipeBinding.inflate(inflater, container, false);
        return this.init(binding);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController= Navigation.findNavController(view);
        dataTransfer = new DataTransfer();
        this.mainActivityViewModel.setBottomBarVisible(true);
        this.mainActivityViewModel.setActionBarVisible(true);

        this.binding.progressBar2.setVisibility(View.VISIBLE);
        this.binding.likeButton.setVisibility(View.GONE);
        this.binding.noLikeButton.setVisibility(View.GONE);
        this.binding.infoButton.setVisibility(View.GONE);

        manager = new CardStackLayoutManager(getContext(),new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                String uuid = uuids.get(manager.getTopPosition()-1);
                dataTransfer.setPairVote(dataTransfer.getUUID(),uuid, direction == Direction.Left).addOnSuccessListener(task->{
                    uuids.remove(uuid);
                    cardStackViewAdapter.updateList(uuids);
                });
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        this.binding.cardStackView.setLayoutManager(manager);
        cardStackViewAdapter = new CardStackViewAdapter(new ArrayList<>(),getContext());
        this.binding.cardStackView.setAdapter(cardStackViewAdapter);
        this.binding.likeButton.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .build();
            manager.setSwipeAnimationSetting(setting);
            binding.cardStackView.swipe();
        });
        this.binding.noLikeButton.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .build();
            manager.setSwipeAnimationSetting(setting);
            binding.cardStackView.swipe();
        });
        this.binding.infoButton.setOnClickListener(v -> {
            if(uuids.isEmpty()){
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(ProfileInfoFragment.KEY,uuids.get(manager.getTopPosition()));
            navController.navigate(R.id.profileInfoFragment,bundle);
        });

        lookForProfiles();

    }

    private void lookForProfiles(){
        dataTransfer.getUsersUIDSInRange(
                dataTransfer.getUUID()
        ).addOnCompleteListener(subTask -> {
            if(subTask.isSuccessful()){
                List<String> ids = subTask.getResult();

                if(ids.isEmpty() && binding!=null){
                    this.binding.noProfilesFound.setVisibility(View.VISIBLE);
                }
                uuids = ids;
                cardStackViewAdapter.updateList(uuids);
                if(binding!=null){
                    if(uuids.isEmpty()){
                        this.binding.likeButton.setVisibility(View.GONE);
                        this.binding.noLikeButton.setVisibility(View.GONE);
                        this.binding.infoButton.setVisibility(View.GONE);
                        this.binding.progressBar2.setVisibility(View.VISIBLE);
                        lookForProfiles();
                    }else{
                        this.binding.likeButton.setVisibility(View.VISIBLE);
                        this.binding.noLikeButton.setVisibility(View.VISIBLE);
                        this.binding.infoButton.setVisibility(View.VISIBLE);
                        this.binding.noProfilesFound.setVisibility(View.GONE);
                        this.binding.progressBar2.setVisibility(View.GONE);
                    }
                }

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