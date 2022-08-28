package com.example.dateapp.models;

import com.example.dateapp.R;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChipItem implements Serializable {
   private String text;
   private Integer drawableId;
   private Integer id;

    public ChipItem(String text, Integer drawableId) {
        this.text = text;
        this.drawableId = drawableId;
        this.id=null;
    }

    public ChipItem(String text) {
        this.text = text;
        this.drawableId= R.drawable.ic_baseline_cancel_24;
        this.id=null;
    }

    public ChipItem() {
        this.drawableId= R.drawable.ic_baseline_cancel_24;
        this.id=null;
    }

    public ChipItem(String text, Integer drawableId, Integer id) {
        this.text = text;
        this.drawableId = drawableId;
        this.id = id;
    }


}
