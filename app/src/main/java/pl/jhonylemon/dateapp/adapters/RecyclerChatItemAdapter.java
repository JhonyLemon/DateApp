package pl.jhonylemon.dateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.models.ChipItem;
import pl.jhonylemon.dateapp.utils.DataTransfer;
import pl.jhonylemon.dateapp.utils.HelperFunctions;

public class RecyclerChatItemAdapter extends RecyclerView.Adapter<RecyclerChatItemAdapter.ViewHolder> {

    private List<String> items;
    private Context context;
    private LayoutInflater inflater;
    private DataTransfer dataTransfer;
    private OnRecyclerItemClick onRecyclerItemClick;

    public RecyclerChatItemAdapter(List<String> items, Context context, @Nullable OnRecyclerItemClick onRecyclerItemClick) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.onRecyclerItemClick = onRecyclerItemClick;
        dataTransfer = new DataTransfer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view,onRecyclerItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.uuid = items.get(position);
        dataTransfer.getName(items.get(position)).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        holder.name.setText(task.getResult().getValue(String.class));
                    }
                });
        dataTransfer.getLastMessage(dataTransfer.getUUID(), holder.uuid).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               String lastMessage = Optional.ofNullable(task.getResult().getValue(String.class)).orElse("");
               holder.lastMessage.setText(lastMessage.substring(0, Math.min(40, lastMessage.length())));
           }
        });
        dataTransfer.getUserPhotos(items.get(position)).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<String> urls = Optional.ofNullable((ArrayList< String >) task.getResult().getValue()).orElse(new ArrayList<>());
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

    public void UpdateList(List<String> items) {
        this.items=items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView lastMessage;
        ImageButton button;
        ShapeableImageView image;
        String uuid;
        ConstraintLayout layout;
        OnRecyclerItemClick onRecyclerItemClick;

        public ViewHolder(@NonNull View itemView,OnRecyclerItemClick onRecyclerItemClick) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
            button = (ImageButton) itemView.findViewById(R.id.delete);
            image = (ShapeableImageView) itemView.findViewById(R.id.profilePicture);
            layout = (ConstraintLayout) itemView.findViewById(R.id.chatBackground);
            button.setOnClickListener(this);
            this.onRecyclerItemClick = onRecyclerItemClick;
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onRecyclerItemClick!=null){
                if(v instanceof ImageButton){
                    onRecyclerItemClick.onButtonItemClick(uuid,getBindingAdapterPosition());
                }else{
                    onRecyclerItemClick.onItemClick(uuid,getBindingAdapterPosition());
                }
            }
        }
    }

    public interface OnRecyclerItemClick {
        void onItemClick(String uuid, Integer position);
        void onButtonItemClick(String uuid, Integer position);
    }
}