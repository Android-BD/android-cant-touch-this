package com.relayr.cannottouchthis.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Switch;

import com.relayr.cannottouchthis.utils.Util;

public class CustomSwitch extends Switch {

    public CustomSwitch(Context context) {
        this(context, null);
    }

    public CustomSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;
        Typeface tf = Util.getFont(context, attrs);
        if (tf != null) setTypeface(tf);
    }
}
