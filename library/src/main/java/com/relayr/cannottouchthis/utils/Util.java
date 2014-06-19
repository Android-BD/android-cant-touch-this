package com.relayr.cannottouchthis.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.relayr.cannottouchthis.R;


public class Util {

    public static boolean isInLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /** @return the font or null if not found */
    public static Typeface getFont(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Fonts, 0, 0);
        Typeface tf = null;
        try {
            String font = typedArray.getString(R.styleable.Fonts_font);
            if (font != null) {
                tf = Typeface.createFromAsset(context.getAssets(), font);
            }
        } finally {
            if (typedArray != null) typedArray.recycle();
        }
        return tf;
    }

}
