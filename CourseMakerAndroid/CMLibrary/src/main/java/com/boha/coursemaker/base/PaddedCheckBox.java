package com.boha.coursemaker.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * This extends the Android CheckBox to add some more padding so the text is not on top of the
 * CheckBox.
 */
public class PaddedCheckBox extends CheckBox {

    public PaddedCheckBox(Context context) {
        super(context);
    }

    public PaddedCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PaddedCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getCompoundPaddingLeft() {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (super.getCompoundPaddingLeft() + (int) (20.0f * scale + 0.5f));
    }
}
