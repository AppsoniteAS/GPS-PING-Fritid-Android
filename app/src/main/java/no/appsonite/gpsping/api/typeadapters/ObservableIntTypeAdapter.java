package no.appsonite.gpsping.api.typeadapters;

import android.databinding.ObservableInt;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class ObservableIntTypeAdapter extends TypeAdapter<ObservableInt> {
    @Override
    public void write(JsonWriter out, ObservableInt value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(value.get());
    }

    @Override
    public ObservableInt read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        int val = in.nextInt();
        return new ObservableInt(val);
    }
}
