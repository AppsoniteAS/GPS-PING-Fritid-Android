package no.appsonite.gpsping.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

import no.appsonite.gpsping.R;


public class CustomFontEditText extends AppCompatEditText {
    private Typeface mCustomFont;

    public CustomFontEditText(Context context) {
        super(context);
    }

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFontName = a.getString(R.styleable.CustomFontTextView_typeface);
        setTypeface(customFontName);
        a.recycle();
    }

    public void setTypeface(String fontName) {
        if (fontName != null) {
            try {
                mCustomFont = Typeface.createFromAsset(getContext().getAssets(), fontName);
                setTypeface(mCustomFont);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}