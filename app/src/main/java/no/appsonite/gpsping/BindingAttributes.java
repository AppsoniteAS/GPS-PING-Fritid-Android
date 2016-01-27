package no.appsonite.gpsping;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import no.appsonite.gpsping.model.Friend;
import no.appsonite.gpsping.utils.CircleTransformation;
import no.appsonite.gpsping.utils.ObservableBoolean;
import no.appsonite.gpsping.utils.ObservableString;

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
                                    final ObservableString observableString) {
        Pair<ObservableString, TextWatcher> pair =
                (Pair) view.getTag(R.id.bound_observable);
        if (pair == null || pair.first != observableString) {
            if (pair != null) {
                view.removeTextChangedListener(pair.second);
            }
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s,
                                          int start, int before, int count) {
                    observableString.set(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            view.setTag(R.id.bound_observable,
                    new Pair<>(observableString, watcher));
            view.addTextChangedListener(watcher);
        }
        if (observableString != null) {
            String newValue = observableString.get();
            if (!view.getText().toString().equals(newValue)) {
                view.setText(newValue);
            }
        }
    }

    @BindingAdapter("app:swipeEnabled")
    public static void setLayoutWidth(SwipeLayout view, boolean enabled) {
        view.setSwipeEnabled(enabled);
    }


    @BindingAdapter({"app:twoWayBoolean"})
    public static void bindCheckBox(CheckBox view, final ObservableBoolean observableBoolean) {
        if (observableBoolean != null) {
            if (view.getTag(R.id.bound_observable) != observableBoolean) {
                view.setTag(R.id.bound_observable, observableBoolean);
                view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        observableBoolean.set(isChecked);
                    }
                });
            }
            Boolean newValue = observableBoolean.get();
            if (newValue != null && view.isChecked() != newValue) {
                view.setChecked(newValue);
            }
        }
    }

    @BindingAdapter({"app:twoWaySpinner"})
    public static void bindSpinner(final Spinner spinner, final ObservableString observable) {
        if (observable != null) {
            if (spinner.getTag(R.id.bound_observable) != observable) {
                spinner.setTag(R.id.bound_observable, observable);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        observable.set((String) spinner.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            String newValue = observable.get();
            if (newValue == null)
                return;
            if (!observable.equals(spinner.getSelectedItem())) {
                SpinnerAdapter spinnerAdapter = spinner.getAdapter();
                int selectedItem = spinner.getSelectedItemPosition();
                if (spinnerAdapter != null) {
                    for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                        if (newValue.equals(spinnerAdapter.getItem(i))) {
                            selectedItem = i;
                            break;
                        }
                    }
                }
                spinner.setSelection(selectedItem);
            }

        }
    }

    @BindingAdapter({"android:entries"})
    public static void setFriendsSpinner(Spinner spinner, ArrayList<Friend> items) {
        ArrayList<String> strings = new ArrayList<>();
        for (Friend item : items) {
            strings.add(item.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), R.layout.item_friends_spinner, strings);
        adapter.setDropDownViewResource(R.layout.item_friends_spinner_drop);
        spinner.setAdapter(adapter);
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
