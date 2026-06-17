package io.github.gohoski.notpipe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

/**
 * Created by Gleb on 16.06.2026.
 */

public class FixedTabHost extends TabHost {
    public FixedTabHost(Context context) {
        super(context);
    }

    public FixedTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        // Prevent NullPointerException on Android 2.1 and below
        // when window focus changes before tabs are loaded.
        if (getCurrentView() != null) {
            super.dispatchWindowFocusChanged(hasFocus);
        }
    }
}