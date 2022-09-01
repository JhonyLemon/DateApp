package pl.jhonylemon.dateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.models.ChipItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerChipsViewAdapter extends RecyclerView.Adapter<RecyclerChipsViewAdapter.ViewHolder> {

    private List<ChipItem> items;
    private Context context;
    private LayoutInflater inflater;
    private OnRecyclerItemClick onRecyclerItemClick;

    private ChipItem addItem;

    public RecyclerChipsViewAdapter(List<ChipItem> items, Context context, @Nullable OnRecyclerItemClick onRecyclerItemClick) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.onRecyclerItemClick = onRecyclerItemClick;
        addItem = new ChipItem(context.getString(R.string.enter_passions_add),R.drawable.ic_baseline_add_circle_24);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chip_text_item, parent, false);
        return new ViewHolder(view, onRecyclerItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position==items.size()){
            holder.text.setText(addItem.getText());
            holder.button.setImageResource(addItem.getDrawableId());
        }
        else if(position<items.size()){
            holder.text.setText(items.get(position).getText());
            holder.button.setImageResource(items.get(position).getDrawableId());
        }
    }

    @Override
    public int getItemCount() {
        return items.size()+1;
    }

    public void UpdateList(List<ChipItem> items) {
        this.items=items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text;
        ImageButton button;
        OnRecyclerItemClick onRecyclerItemClick;

        public ViewHolder(@NonNull View itemView, OnRecyclerItemClick onRecyclerItemClick) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.recyclerItemTextView);
            button = (ImageButton) itemView.findViewById(R.id.recyclerItemImageButton);
            this.onRecyclerItemClick = onRecyclerItemClick;
            button.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onRecyclerItemClick != null && v instanceof ImageButton) {
                onRecyclerItemClick.onItemClick(text.getText().toString(), getBindingAdapterPosition());
            }
        }
    }

    public interface OnRecyclerItemClick {
        public void onItemClick(String value, Integer position);
    }
}