package com.example.dateapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.DrawableUtils;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.dateapp.R;
import com.example.dateapp.helpers.BitmapDrawable;


import java.util.ArrayList;
import java.util.List;

public class GridViewPhotosAdapter extends ArrayAdapter<Bitmap> {

    private List<Bitmap> photos;
    private Context context;
    private LayoutInflater inflater;
    private OnRecyclerItemClick onRecyclerItemClick;
    private Bitmap addButton;
    private Integer width;
    private Integer height;

    public GridViewPhotosAdapter(List<Bitmap> photos, Context context, OnRecyclerItemClick onRecyclerItemClick) {
        super(context, 0);
            this.photos = photos;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.onRecyclerItemClick = onRecyclerItemClick;

        //addButton=BitmapFactory.decodeResource(getContext().getResources(),R.drawable.ic_baseline_add_24);

        addButton=BitmapDrawable.getBitmap(context,R.drawable.ic_baseline_add_24);
    }


    @Override
    public int getCount() {
        if(photos.size()<9){
            return photos.size()+1;
        }else{
            return photos.size();
        }

    }

    public void setSize(Integer width, Integer height){
        this.width=width;
        this.height=height;
        notifyDataSetChanged();
    }

    @Override
    public Bitmap getItem(int position) {
        if(position==photos.size() && photos.size()<9){
            return addButton;
        }else{
            return photos.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setPhotos(List<Bitmap> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.gridview_photo_item, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.itemLayout = (ConstraintLayout) convertView.findViewById(R.id.itemLayout);
            viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.removePhotoButton);
            convertView.setTag(viewHolder);
        }else {
            viewHolder =(ViewHolderItem) convertView.getTag();
            if(width!=null && height !=null)
            {
                android.view.ViewGroup.LayoutParams  params = viewHolder.itemLayout.getLayoutParams();
                params.height=height.intValue();
                params.width=width.intValue();
                viewHolder.itemLayout.setLayoutParams(params);
            }
            if(position==photos.size() && photos.size()<9){
                viewHolder.imageView.setImageBitmap(addButton);
                viewHolder.imageButton.setVisibility(View.GONE);
                viewHolder.imageView.setOnClickListener(v -> onRecyclerItemClick.onItemClick(position));
            }else{
                Bitmap currentItem = photos.get(position);
                viewHolder.imageView.setImageBitmap(currentItem);
                viewHolder.imageButton.setVisibility(View.VISIBLE);
                viewHolder.imageButton.setOnClickListener(v -> onRecyclerItemClick.onItemClick(position));
            }
        }
        return convertView;
    }

    public interface OnRecyclerItemClick {
        public void onItemClick(Integer position);
    }

    static class ViewHolderItem {
        ImageButton imageButton;
        ConstraintLayout itemLayout;
        ImageView imageView;
    }



}
