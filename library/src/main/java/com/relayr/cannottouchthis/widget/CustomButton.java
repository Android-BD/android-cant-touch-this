package com.relayr.cannottouchthis.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.relayr.cannottouchthis.utils.Util;

public class CustomButton extends Button {

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;
        Typeface tf = Util.getFont(context, attrs);
        if (tf != null) setTypeface(tf);
    }
}
