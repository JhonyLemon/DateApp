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

import java.util.List;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.models.ChipItem;

public class RecyclerChipViewAdapter extends RecyclerView.Adapter<RecyclerChipViewAdapter.ViewHolder> {

    private List<ChipItem> items;
    private Context context;
    private LayoutInflater inflater;

    public RecyclerChipViewAdapter(List<ChipItem> items, Context context) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chip_text_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(items.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void UpdateList(List<ChipItem> items) {
        this.items=items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.recyclerItemTextView);
        }

    }
}