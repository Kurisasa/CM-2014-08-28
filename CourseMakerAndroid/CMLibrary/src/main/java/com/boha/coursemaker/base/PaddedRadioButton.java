package com.boha.coursemaker.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * This extends the Android RadioButton to add some more padding so the text is not on top of the
 * CheckBox.
 */
public class PaddedRadioButton extends RadioButton {

    public PaddedRadioButton(Context context) {
        super(context);
    }

    public PaddedRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PaddedRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getCompoundPaddingLeft() {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (super.getCompoundPaddingLeft() + (int) (30.0f * scale + 0.5f));
    }
    @Override
    public int getCompoundPaddingRight() {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (super.getCompoundPaddingRight() + (int) (30.0f * scale + 0.5f));
    }
}
