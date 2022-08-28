package com.example.dateapp.helpers

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap

class BitmapDrawable {
    companion object{
        @JvmStatic
        fun getBitmap(context: Context, drawableId: Int) : Bitmap{
            return AppCompatResources.getDrawable(context, drawableId)!!.toBitmap()
        }
    }
}