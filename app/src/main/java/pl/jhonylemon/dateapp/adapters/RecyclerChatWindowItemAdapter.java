package pl.jhonylemon.dateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.entity.UserChatMessage;
import pl.jhonylemon.dateapp.utils.DataTransfer;

public class RecyclerChatWindowItemAdapter extends RecyclerView.Adapter<RecyclerChatWindowItemAdapter.ViewHolder> {

    private List<UserChatMessage> items;
    private Context context;
    private LayoutInflater inflater;
    private DataTransfer dataTransfer;

    public RecyclerChatWindowItemAdapter(List<UserChatMessage> items, Context context) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        dataTransfer = new DataTransfer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_window_bubble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(items.get(position).getUuid().equals(dataTransfer.getUUID())){
            holder.myMessage.setText(items.get(position).getMessage());
            holder.theirMessage.setVisibility(View.GONE);
            holder.myMessage.setVisibility(View.VISIBLE);
        }else{
            holder.theirMessage.setText(items.get(position).getMessage());
            holder.myMessage.setVisibility(View.GONE);
            holder.theirMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void UpdateList(List<UserChatMessage> items) {
        this.items=items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView myMessage;
        TextView theirMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = (TextView) itemView.findViewById(R.id.myText);
            theirMessage = (TextView) itemView.findViewById(R.id.theirText);
        }

    }

}