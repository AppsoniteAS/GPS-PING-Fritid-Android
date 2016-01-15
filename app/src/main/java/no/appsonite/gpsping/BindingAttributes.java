package no.appsonite.gpsping;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import no.appsonite.gpsping.utils.BindableString;
import no.appsonite.gpsping.utils.CircleTransformation;

/**
 * Created by Belozerow on 11.10.2015.
 */
public class BindingAttributes {
    private static HashMap<String, Typeface> typefaceHashMap = new HashMap<>();

    @BindingAdapter({"bind:circleUrl"})
    public static void setCircleUrl(ImageView imageView, String url) {
        if (url == null)
            return;
        Picasso.with(imageView.getContext()).load(url).transform(new CircleTransformation()).into(imageView);
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void setImageUrl(ImageView imageView, String url) {
        if (url == null)
            return;
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

    @BindingAdapter("bind:customFont")
    public static void setCustomFont(TextView textView, String font) {
        Typeface tf = typefaceHashMap.get(font);
        if (tf == null) {
            tf = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + font);
            typefaceHashMap.put(font, tf);
        }
        textView.setTypeface(tf);
    }


    @BindingAdapter({"bind:imageRes"})
    public static void setImageRes(ImageView imageView, int res) {
        imageView.setImageResource(res);
    }

    @BindingAdapter({"bind:imageResName"})
    public static void setImageResName(ImageView imageView, String resName) {
        Context context = imageView.getContext();
        if (TextUtils.isEmpty(resName)) {
            return;
        }
        int id = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        imageView.setImageResource(id);
    }

    @BindingAdapter({"app:twoWayText"})
    public static void bindEditText(EditText view,
                                    final BindableString bindableString) {
        Pair<BindableString, TextWatcher> pair =
                (Pair) view.getTag(R.id.bound_observable);
        if (pair == null || pair.first != bindableString) {
            if (pair != null) {
                view.removeTextChangedListener(pair.second);
            }
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s,
                                          int start, int before, int count) {
                    bindableString.set(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            view.setTag(R.id.bound_observable,
                    new Pair<>(bindableString, watcher));
            view.addTextChangedListener(watcher);
        }
        String newValue = bindableString.get();
        if (!view.getText().toString().equals(newValue)) {
            view.setText(newValue);
        }
    }

    @BindingAdapter({"app:error"})
    public static void setTextInputError(TextInputLayout textInputLayout, String error) {
        if (TextUtils.isEmpty(error)) {
            textInputLayout.setError(null);
        } else {
            textInputLayout.setError(error);
        }
    }

    @BindingAdapter({"app:onActionDone"})
    public static void setOnDoneListener(EditText editText, TextView.OnEditorActionListener listener) {
        editText.setOnEditorActionListener(listener);
    }
}
