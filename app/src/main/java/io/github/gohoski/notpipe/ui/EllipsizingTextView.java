package io.github.gohoski.notpipe.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import io.github.gohoski.notpipe.NotPipe;

/**
 * Created by Gleb on 11.06.2026.
 * Custom TextView that handles multi-line ellipsizing.
 * On API < 11 (Android 1.x/2.x), it acts as a standard TextView to avoid
 * StackOverflowErrors on devices with tiny thread stacks (8 KB).
 */
public class EllipsizingTextView extends TextView {
    private static final String ELLIPSIS = "...";

    private boolean isStale = true;
    private boolean programmaticChange = false;
    private String fullText = "";
    private int maxLines = -1;
    private static final boolean IS_HONEYCOMB_OR_NEWER = NotPipe.SDK >= 11;

    public EllipsizingTextView(Context context) {
        super(context);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(attrs);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttributes(attrs);
    }

    private void readAttributes(AttributeSet attrs) {
        if (attrs != null) {
            // Extract android:maxLines directly from XML
            int androidMaxLines = attrs.getAttributeIntValue(
                    "http://schemas.android.com/apk/res/android", "maxLines", -1);
            if (androidMaxLines != -1) {
                this.maxLines = androidMaxLines;
            } else {
                // Fallback to android:lines if maxLines is not set
                int androidLines = attrs.getAttributeIntValue(
                        "http://schemas.android.com/apk/res/android", "lines", -1);
                if (androidLines != -1) {
                    this.maxLines = androidLines;
                }
            }
        }
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        this.maxLines = maxLines;
        isStale = true;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!programmaticChange) {
            fullText = text.toString();
            isStale = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (IS_HONEYCOMB_OR_NEWER) {
            if (isStale) {
                resetText();
            }
        }
        super.onDraw(canvas);
    }

    private void resetText() {
        if (isInEditMode()) {
            isStale = false;
            return;
        }
        String workingText = fullText;
        if (maxLines != -1) {
            Layout layout = createWorkingLayout(workingText);
            if (layout.getLineCount() > maxLines) {
                int lastLineEnd = layout.getLineEnd(maxLines - 1);
                workingText = fullText.substring(0, lastLineEnd).trim();

                while (createWorkingLayout(workingText + ELLIPSIS).getLineCount() > maxLines) {
                    int lastSpace = workingText.lastIndexOf(' ');
                    if (lastSpace == -1) {
                        if (workingText.length() > 0) {
                            workingText = workingText.substring(0, workingText.length() - 1);
                        } else {
                            break;
                        }
                    } else {
                        workingText = workingText.substring(0, lastSpace);
                    }
                }
                workingText = workingText + ELLIPSIS;
            }
        }
        if (!workingText.equals(getText().toString())) {
            programmaticChange = true;
            try {
                setText(workingText);
            } finally {
                programmaticChange = false;
            }
        }
        isStale = false;
    }

    private Layout createWorkingLayout(String workingText) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        if (width <= 0) {
            width = 100;
        }
        return new StaticLayout(
                workingText,
                getPaint(),
                width,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                false
        );
    }
}