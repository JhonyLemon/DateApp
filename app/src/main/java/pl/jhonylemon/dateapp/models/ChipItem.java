package pl.jhonylemon.dateapp.models;

import androidx.annotation.Nullable;

import pl.jhonylemon.dateapp.R;

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

    public ChipItem(String text, @Nullable Integer drawableId, Integer id) {
        this.text = text;
        this.drawableId = drawableId==null ? R.drawable.ic_baseline_cancel_24 : drawableId;
        this.id = id;
    }


}
