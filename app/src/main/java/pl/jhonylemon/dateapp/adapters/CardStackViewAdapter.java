package pl.jhonylemon.dateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.models.ChipItem;
import pl.jhonylemon.dateapp.models.StackCardCard;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class CardStackViewAdapter extends RecyclerView.Adapter<CardStackViewAdapter.ViewHolder> {

    private List<String> items;
    private final Context context;
    private final LayoutInflater inflater;
    private final DataTransfer dataTransfer;

    public CardStackViewAdapter(List<String> items, Context context) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.dataTransfer = new DataTransfer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.swipe_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dataTransfer.getName(items.get(position)).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        holder.name.setText(task.getResult().getValue(String.class));
                    }
                });
        dataTransfer.getDescription(items.get(position)).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String description = Optional.ofNullable(task.getResult().getValue(String.class)).orElse("");
                        holder.description.setText(description.substring(0, Math.min(100, description.length())));
                    }
                });
        dataTransfer.getUserPhotos(items.get(position)).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<String> urls = Optional.ofNullable((ArrayList < String >) task.getResult().getValue()).orElse(new ArrayList<>());
                        Glide.with(context)
                                .load(urls.stream().findFirst().orElse(""))
                                .signature(new ObjectKey(urls.stream().findFirst().orElse("")))
                                .into(holder.image);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<String> items) {
        this.items=items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView description;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.user_name);
            description = (TextView) itemView.findViewById(R.id.user_description);
            image = (ImageView) itemView.findViewById(R.id.item_image);
        }

    }
}