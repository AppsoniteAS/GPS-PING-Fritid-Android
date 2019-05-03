package no.appsonite.gpsping.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import no.appsonite.gpsping.Application;
import no.appsonite.gpsping.BuildConfig;
import no.appsonite.gpsping.R;
import no.appsonite.gpsping.WTFileProvider;
import no.appsonite.gpsping.amazon.AmazonFileLoader;
import no.appsonite.gpsping.api.ApiFactory;
import no.appsonite.gpsping.databinding.FragmentEditTrackerBinding;
import no.appsonite.gpsping.managers.ProfileUpdateManager;
import no.appsonite.gpsping.model.SMS;
import no.appsonite.gpsping.model.Tracker;
import no.appsonite.gpsping.utils.FileUtils;
import no.appsonite.gpsping.utils.ImageUtils;
import no.appsonite.gpsping.utils.image_transformation.BitmapRotator;
import no.appsonite.gpsping.viewmodel.EditTrackerFragmentViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

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
        getSMSPermission();
    }

    private void initBlocks() {
        initStartBtn();
        initStopBtn();
        initSignalBlock();
        initSleepModeBlock();
        initBikeTrackingBlock();
        initGeofenceBlock();
        initTrackerHistoryBlock();
        initUploadPhotoBtn();
        initUpdateBtn();
        initResetBtn();
        initShutDownBtn();
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
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RESULT_LOAD_IMG);
        } else {
            startActivityForResult(getModel().getImagePickerIntent(), RESULT_LOAD_IMG);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMG:
                loadPhoto(data, resultCode);
                break;
            case PERMISSION_SMS:
                getSMSPermission();
                break;
            case PERMISSION_STORAGE:
                actionSelect();
                break;
            default:
                break;
        }
    }

    private void loadPhoto(Intent data, int resultCode) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            uploadForApi15(data, resultCode);
        } else {
            uploadResult(data, resultCode);
        }
    }

    private void uploadForApi15(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri imageUri = data.getData();
            Bitmap selectedImage = null;
            try {
                if (imageUri != null) {
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    selectedImage = ImageUtils.compressBitmap(selectedImage, 960, 960);
                    if (imageStream != null) {
                        imageStream.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String path = getRealPathFromURI(getImageUri(selectedImage));
                if (path != null) {
                    selectedImage = rotateImage(selectedImage, path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getBinding().photo.setImageBitmap(selectedImage);
            uploadPhotoToAmazon(getImageUri(selectedImage).toString());
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, String path) {
        BitmapRotator bitmapRotator = new BitmapRotator();
        return bitmapRotator.rotateBitmapIfNecessary(path, bitmap);
    }

    public String getRealPathFromURI(Uri uri) {
        String string = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                string = cursor.getString(idx);
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return string;
    }

    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void uploadResult(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }

            Uri selectedImageUri;
            if (isCamera) {
                selectedImageUri = getModel().outputFileUri;
            } else {
                selectedImageUri = data.getData();
                if (selectedImageUri == null) {
                    selectedImageUri = getModel().outputFileUri;
                }
            }

            try {
                Uri uri = scaleUserPic(selectedImageUri.toString());
                getBinding().photo.setImageURI(uri);
                uploadPhotoToAmazon(uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Uri scaleUserPic(String userPic) {
        File file;
        try {
            file = FileUtils.getFile(Application.getContext(), Uri.parse(userPic));
            if (file == null || !file.exists())
                file = ProfileUpdateManager.copyFileFromUri(userPic);
        } catch (Exception e) {
            file = ProfileUpdateManager.copyFileFromUri(userPic);
        }
        File uploadFile = ProfileUpdateManager.loadImage(file, 960, 960);
        return FileProvider.getUriForFile(getContext(), WTFileProvider.AUTHORITY, uploadFile);
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
            Picasso.with(getContext()).load(url).resize(720, 720).centerCrop().into(getBinding().photo);
        }
    }

    private void getSMSPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // show warning
            PermissionsDialogFragment permissionsDialogFragment = new PermissionsDialogFragment();
            permissionsDialogFragment.show(getChildFragmentManager(), "permissions");
            permissionsDialogFragment.setNegativeButtonClickListener((dialog, which) -> getSMSPermissionOnNext(false));
            permissionsDialogFragment.setPositiveButtonClickListener((dialog, which) ->
                    new RxPermissions(getActivity())
                            .request(SEND_SMS, READ_SMS, RECEIVE_SMS, READ_PHONE_STATE)
                            .subscribe(this::getSMSPermissionOnNext)
            );
        }
    }

    private void getSMSPermissionOnNext(boolean granted) {
        if (granted) {

        } else {
            showInfoDeniedPermission(getContext(), PERMISSION_SMS, SEND_SMS, READ_SMS, RECEIVE_SMS, READ_PHONE_STATE);
//            getBaseActivity().onBackPressed();
        }
    }

    private void initResetBtn() {
        getBinding().resetButton.setOnClickListener(view -> resetTracker());
    }

    private void resetTracker() {
        Observable<SMS> observable = getModel().resetTracker(getActivity());
        if (observable != null) {
            showProgress();
            observable.subscribe(sms -> resetTrackerOnNext(), this::showError);
        }
    }

    private void resetTrackerOnNext() {
        hideProgress();
        Toast.makeText(getActivity(), getString(R.string.trackerUpdated), Toast.LENGTH_SHORT).show();
    }

    private void initShutDownBtn() {
        getBinding().shutDownBtn.setOnClickListener(v -> shutDownBtn());
    }

    private void shutDownBtn() {
        if (getModel().tracker.get() == null)
            return;
        showProgress();
        getModel().shutDown(getActivity()).subscribe(sms -> {

        }, this::showError, this::hideProgress);
    }
}
