package no.appsonite.gpsping.viewmodel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import no.appsonite.gpsping.R;
import no.appsonite.gpsping.model.Friend;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class TrackersMapFragmentViewModel extends BaseFragmentViewModel {
    public ObservableArrayList<Friend> friendList = new ObservableArrayList<>();

    public void requestFriends() {

    }

    public void saveBitmap(final Bitmap bitmap) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return saveBitmapToGallery(bitmap);
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String filePath) {
                        notifyBitmapSaved(filePath);
                    }
                });
    }

    private void notifyBitmapSaved(String filePath) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 12, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(getContext().getString(R.string.notify_photo_title))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(getContext().getString(R.string.notify_photo_text));
        notificationManager.notify(100, builder.build());
    }

    private Observable<String> saveBitmapToGallery(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytes);
        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator + getContext().getString(R.string.app_name) + File.separator);
        directory.mkdirs();
        File outputFile = new File(directory, new Date().toString() + ".png");
        try {
            outputFile.createNewFile();
            FileOutputStream fo = new FileOutputStream(outputFile);
            fo.write(bytes.toByteArray());
            fo.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, outputFile.getPath());
            values.put(MediaStore.Images.Media.DATE_TAKEN, outputFile.lastModified());
            if (getContext().getContentResolver() != null) {
                getContext().getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                getContext().getContentResolver().notifyChange(
                        Uri.parse("file://" + outputFile.getPath()), null);
            }
            return Observable.just(outputFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
