package com.example.dateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dateapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerSearchableDefaultViewAdapter extends RecyclerView.Adapter<RecyclerSearchableDefaultViewAdapter.ViewHolder> implements Filterable {

    private List<String>  items;
    private List<String> tempItems;
    private Context context;
    private LayoutInflater inflater;
    private OnRecyclerItemClick onRecyclerItemClick;
    private Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(items);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String s : items) {
                    if (s.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(s);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tempItems= (List<String>) results.values;
            notifyDataSetChanged();
        }
    };

    public RecyclerSearchableDefaultViewAdapter(ArrayList<String> items, Context context,@Nullable OnRecyclerItemClick onRecyclerItemClick) {
        this.items = items;
        this.tempItems = items;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.onRecyclerItemClick=onRecyclerItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.simple_recycler_item, parent, false);
        return new ViewHolder(view,onRecyclerItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(tempItems.get(position));
    }

    public void UpdateList(ArrayList<String> items) {
        this.items=items;
        this.tempItems=items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tempItems.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text;
        OnRecyclerItemClick onRecyclerItemClick;

        public ViewHolder(@NonNull View itemView,OnRecyclerItemClick onRecyclerItemClick) {
            super(itemView);
            text = (TextView)itemView.findViewById(R.id.recyclerItemTextView);
            text.setOnClickListener(this);
            this.onRecyclerItemClick=onRecyclerItemClick;
        }

        @Override
        public void onClick(View v) {
            if(onRecyclerItemClick!=null && v instanceof TextView)
                onRecyclerItemClick.onItemClick(((TextView) v).getText().toString(),getBindingAdapterPosition());
        }
    }

    public interface OnRecyclerItemClick {
        public void onItemClick(String value,Integer position);
    }

}



