package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import no.appsonite.gpsping.BuildConfig;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.amazon.AmazonFileLoader;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.api.AuthHelper;
import no.appsonite.gpsping.api.content.Profile;
import no.appsonite.gpsping.databinding.FragmentEditTrackerBinding;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.ImageUtils;
import no.appsonite.gpsping.viewmodel.EditTrackerFragmentViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 18.01.2016
 */
public class EditTrackerFragment extends BaseBindingFragment<FragmentEditTrackerBinding, EditTrackerFragmentViewModel> {
    private static final String TAG = EditTrackerFragment.class.getSimpleName();
    private static final String ARG_TRACKER = "arg_tracker";
    private static final int RESULT_LOAD_IMG = 1;
    private static final int PERMISSION_SMS = 2;
    private static final int PERMISSION_STORAGE = 3;

    public static EditTrackerFragment newInstance(Tracker tracker) {
        Bundle args = new Bundle();
        EditTrackerFragment fragment = new EditTrackerFragment();
        args.putSerializable(ARG_TRACKER, tracker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void onViewModelCreated(EditTrackerFragmentViewModel model) {
        super.onViewModelCreated(model);
        model.tracker.set((Tracker) getArguments().getSerializable(ARG_TRACKER));
        initBlocks();
        getPermission();
    }

    private void initBlocks() {
        initStartBtn();
        initStopBtn();
        initSignalBlock();
        initSleepModeBlock();
        initBikeTrackingBlock();
        initGeofenceBlock();
        initResetBtn();
        initTrackerHistoryBlock();
        initUploadPhotoBtn();
        initUpdateBtn();
        initPauseSubscriptionBtn();
        downloadPhoto();
    }

    private void initStartBtn() {
        getBinding().startBtn.setOnClickListener(v -> startButtonClick());
    }

    private void startButtonClick() {
        if (getModel().tracker.get() == null)
            return;
        showProgress();
        getModel().startTracker(getActivity()).subscribe(sms -> {

        }, this::showError, this::hideProgress);
    }

    private void initStopBtn() {
        getBinding().stopBtn.setOnClickListener(v -> stopButtonClick());
    }

    private void stopButtonClick() {
        if (getModel().tracker.get() == null)
            return;
        showProgress();
        getModel().stopTracker(getActivity()).subscribe(sms -> {

        }, this::showError, this::hideProgress);
    }

    private void initSignalBlock() {
        final String[] variantsValues = getModel().tracker.get().getEntriesValues();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getModel().tracker.get().getEntriesText());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getBinding().signalTimeSpinner.setAdapter(spinnerArrayAdapter);

        String repeatTime = getModel().tracker.get().signalRepeatTime.get();
        int selected = 4;
        for (int i = 0; i < variantsValues.length; i++) {
            if (variantsValues[i].equals(repeatTime)) {
                selected = i;
                break;
            }
        }

        getBinding().signalTimeSpinner.setSelection(Math.min(selected, variantsValues.length - 1));
        getBinding().signalTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getModel().tracker.get().signalRepeatTime.set(variantsValues[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSleepModeBlock() {
        getBinding().checkBattery.setOnClickListener(v -> {
            showProgress();
            getModel().checkBattery(getActivity()).subscribe(sms -> {

            }, this::showError, this::hideProgress);
        });

        getBinding().sleepMode.setOnClickListener(v -> {
            getModel().tracker.get().sleepMode.set(!getModel().tracker.get().sleepMode.get());
            Observable<Boolean> observable = getModel().updateSleepMode(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgress);
            }
        });
    }

    private void initBikeTrackingBlock() {
        getBinding().ledActive.setOnClickListener(view -> {
            getModel().tracker.get().ledActive.set(!getModel().tracker.get().ledActive.get());
            Observable<Boolean> observable = getModel().updateLed(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgressAndShowToastTrackerUpdated);
            }
        });

        getBinding().shockAlarmActive.setOnClickListener(view -> {
            getModel().tracker.get().shockAlarmActive.set(!getModel().tracker.get().shockAlarmActive.get());
            Observable<Boolean> observable = getModel().updateShockAlarm(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgressAndShowToastTrackerUpdated);
            }
        });

        getBinding().shockFlashActive.setOnClickListener(view -> {
            getModel().tracker.get().shockFlashActive.set(!getModel().tracker.get().shockFlashActive.get());
            Observable<Boolean> observable = getModel().updateShockFlashAlarm(getActivity());
            if (observable != null) {
                showProgress();
                observable.subscribe(aBoolean -> {

                }, this::showError, this::hideProgressAndShowToastTrackerUpdated);
            }
        });
    }

    private void initGeofenceBlock() {
        getBinding().startGeofenceBtn.setOnClickListener(v -> startGeofence());
    }

    private void startGeofence() {
        Observable<SMS> observable = getModel().switchGeofence(getActivity());
        if (observable != null) {
            showProgress();
            observable.subscribe(sms -> {

            }, this::showError, this::hideProgress);
        }
    }

    private void initTrackerHistoryBlock() {
        getBinding().saveTrackingHistory.setOnClickListener(v -> saveTrackingHistory());
    }

    private void saveTrackingHistory() {
        getModel().saveTrackingHistory();
        Toast.makeText(getActivity(), getString(R.string.trackerHistoryUpdated), Toast.LENGTH_SHORT).show();
    }

    private void initUploadPhotoBtn() {
        getBinding().uploadPhotoBtn.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v, Gravity.CENTER);
        popupMenu.inflate(R.menu.menu_choice_action);
        popupMenu.setOnMenuItemClickListener(item -> selectAction(item.getItemId()));
        popupMenu.show();
    }

    private boolean selectAction(int itemId) {
        switch (itemId) {
            case R.id.action_select:
                actionSelect();
                break;
            case R.id.action_delete:
                actionDelete();
                break;
        }
        return true;
    }

    private void actionSelect() {
        new RxPermissions(getActivity())
                .request(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
                .subscribe(this::actionSelectOnNext);
    }

    private void actionSelectOnNext(boolean granted) {
        if (granted) {
            uploadPhoto();
        } else {
            showInfoDeniedPermission(getContext(), PERMISSION_STORAGE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);
        }
    }

    private void actionDelete() {
        deletePhoto();
    }

    private void deletePhoto() {
        showProgress();
        ApiFactory.getService().deleteImage(getModel().tracker.get().imeiNumber.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiAnswer -> {

                }, this::showError, this::deletePhotoCompleted);
    }

    private void deletePhotoCompleted() {
        getBinding().photo.setImageBitmap(null);
        hideProgress();
    }

    private void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RESULT_LOAD_IMG);
    }

    private void initUpdateBtn() {
        getBinding().updateTrackerBtn.setOnClickListener(v -> updateTracker());
    }

    private void updateTracker() {
        Observable<Boolean> observable = getModel().updateTracker(getActivity());
        if (observable != null) {
            showProgress();
            observable.subscribe(isNew -> {
                hideProgress();
                getBaseActivity().getSupportFragmentManager().popBackStack();
                if (!isNew)
                    showToastTrackerUpdated();
            }, this::showError);
        }
    }

    private void showToastTrackerUpdated() {
        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
    }

    private void hideProgressAndShowToastTrackerUpdated() {
        hideProgress();
        showToastTrackerUpdated();
    }

    private void initPauseSubscriptionBtn() {
        getBinding().pauseSubscriptionBtn.setOnClickListener(view -> {
            Tracker tracker = getModel().tracker.get();
            String imei = tracker.imeiNumber.get();
            String phone = tracker.trackerNumber.get();
            Profile user = AuthHelper.getCredentials().getUser();
            String email = "support@gpsping.no";
            String body = getString(R.string.pauseSubscriptionEmailBody, user.firstName.get() + " " + user.lastName.get(), user.address.get(), user.username.get(), imei, phone);
            String subject = getString(R.string.pauseSubscription);


            Intent gmail = new Intent(Intent.ACTION_VIEW);
            gmail.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            gmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            gmail.putExtra(Intent.EXTRA_SUBJECT, subject);
            gmail.setType("plain/text");
            gmail.putExtra(Intent.EXTRA_TEXT, body);
            try {
                startActivity(gmail);
            } catch (Exception e) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, subject);
                i.putExtra(Intent.EXTRA_TEXT, body);

                startActivity(Intent.createChooser(i, getString(R.string.pauseSubscription)));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMG:
                uploadResult(data);
                break;
            case PERMISSION_SMS:
                getPermission();
                break;
            case PERMISSION_STORAGE:
                actionSelect();
                break;
            default:
                break;
        }
    }

    private void uploadResult(Intent data) {
        try {
            Uri imageUri = data.getData();

            String path = imageUri.toString();
            uploadPhotoToAmazon(path);

            InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            selectedImage = ImageUtils.compressBitmap(selectedImage, 1280, 960);

            getBinding().photo.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void uploadPhotoToAmazon(String path) {
        String uuid = UUID.randomUUID().toString() + ".jpg";
        TransferObserver observer = AmazonFileLoader.uploadPhoto(path, uuid);
        showProgress();
        observer.setTransferListener(new TransferListener() {
            boolean isDone = false;

            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                if (bytesCurrent == bytesTotal && !isDone) {
                    isDone = true;
                    uploadUUIDToServer(uuid);
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(getContext(), getString(R.string.could_not_upload_image), Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });
    }

    private void uploadUUIDToServer(String uuid) {
        Log.i(TAG, "uuid = " + uuid);
        ApiFactory.getService().updateImage(getModel().tracker.get().imeiNumber.get(), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiAnswer -> {

                }, this::showError, this::hideProgress);
    }

    private void downloadPhoto() {
        if (getModel().tracker.get().picUrl != null) {
            String url = BuildConfig.AMAZON_ADDRESS + getModel().tracker.get().picUrl.get();
            Picasso.with(getContext()).load(url).fit().into(getBinding().photo);
        }
    }

    private void initResetBtn() {
//        getBinding().resetButton.setOnClickListener(view -> resetTracker());
    }

    private void getPermission() {
        new RxPermissions(getActivity())
                .request(SEND_SMS, READ_SMS, RECEIVE_SMS)
                .subscribe(this::getPermissionOnNext);
    }

    private void getPermissionOnNext(boolean granted) {
        if (granted) {

        } else {
            showInfoDeniedPermission(getContext(), PERMISSION_SMS, SEND_SMS, READ_SMS, RECEIVE_SMS);
        }
    }

    //    private void resetTracker() {
//        Observable<SMS> observable = getModel().resetTracker(getActivity());
//        if (observable != null) {
//            showProgress();
//            observable.subscribe(new Observer<SMS>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    showError(e);
//                }
//
//                @Override
//                public void onNext(SMS sms) {
//                    hideProgress();
//                    Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
}
