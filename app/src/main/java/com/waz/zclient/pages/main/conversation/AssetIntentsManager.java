/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.pages.main.conversation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import com.waz.zclient.BuildConfig;
import com.waz.zclient.utils.PermissionUtils;
import timber.log.Timber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AssetIntentsManager {
    private static final String SAVED_STATE_PENDING_URI = "SAVED_STATE_PENDING_URI";

    private static final String[] CAMERA_PERMISSIONS = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final String INTENT_GALLERY_TYPE = "image/*";
    private final PackageManager pm;

    @TargetApi(19)
    private static String openDocumentAction() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;
    }

    private Uri pendingFileUri;
    private Callback callback;

    public AssetIntentsManager(Activity activity, Callback callback, Bundle savedInstanceState) {
        setCallback(callback);

        if (savedInstanceState != null) {
            pendingFileUri = savedInstanceState.getParcelable(SAVED_STATE_PENDING_URI);
        }
        pm = activity.getPackageManager();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void openDocument(String mimeType, IntentType tpe) {
        if (BuildConfig.IS_TEST_GALLERY_ALLOWED) {
            // trying to load file from testing gallery,
            // this is needed because we are not able to override DocumentsUI on some android versions.
            Intent intent = new Intent("com.wire.testing.GET_DOCUMENT").setType(mimeType);
            if (!pm.queryIntentActivities(intent, PackageManager.MATCH_ALL).isEmpty()) {
                callback.openIntent(intent, tpe);
                return;
            }
            Timber.i("Did not resolve testing gallery for intent: %s", intent.toString());
        }
        callback.openIntent(new Intent(openDocumentAction()).setType(mimeType).addCategory(Intent.CATEGORY_OPENABLE), tpe);
    }

    public void openFileSharing() {
        openDocument("*/*", IntentType.FILE_SHARING);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pendingFileUri = getOutputMediaFileUri(IntentType.CAMERA);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pendingFileUri);
        callback.openIntent(intent, IntentType.CAMERA);
    }

    public void maybeCaptureVideo(Activity activity, IntentType type) {
        if (PermissionUtils.hasSelfPermissions(activity, CAMERA_PERMISSIONS)) {
            captureVideo(type);
        } else {
            ActivityCompat.requestPermissions(activity, CAMERA_PERMISSIONS, type.permissionCode);
        }
    }

    private void captureVideo(IntentType type) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        pendingFileUri = getOutputMediaFileUri(IntentType.VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pendingFileUri);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        }
        callback.openIntent(intent, type);
    }

    public void openGallery() {
        openDocument(INTENT_GALLERY_TYPE, IntentType.GALLERY);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        if (callback == null) {
            throw new IllegalStateException("A callback must be set!");
        }

        IntentType type = IntentType.get(requestCode);

        if (type == IntentType.UNKOWN) {
            return false;
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            callback.onCanceled(type);
            return true;
        }

        if (resultCode != Activity.RESULT_OK) {
            callback.onFailed(type);
            return true;
        }

        File possibleFile = null;
        if (pendingFileUri != null) {
            possibleFile = new File(pendingFileUri.getPath());
        }
        if ((type == IntentType.CAMERA || type == IntentType.VIDEO || type == IntentType.VIDEO_CURSOR_BUTTON) &&
            possibleFile != null &&
            possibleFile.exists() &&
            possibleFile.length() > 0) {
                callback.onDataReceived(type, pendingFileUri);
            pendingFileUri = null;
        } else if (data != null) {
            Uri uri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                uri = Uri.parse(data.getDataString());
            } else {
                uri = data.getData();
            }
            if (uri == null) {
                callback.onFailed(type);
            } else {
                callback.onDataReceived(type, uri);
            }
        } else {
            callback.onFailed(type);
        }

        return true;
    }

    /**
     * Create a file Uri for saving an image or video
     *
     * @param type
     */
    private static Uri getOutputMediaFileUri(IntentType type) {
        File file = getOutputMediaFile(type);
        return file != null ? Uri.fromFile(file) : null;
    }

    /**
     * Create a File for saving an image or video
     *
     * @param type
     */
    private static File getOutputMediaFile(IntentType type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "WIRE_MEDIA");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            return null;
        }

        java.util.Date date = new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date.getTime());

        switch (type) {
            case VIDEO_CURSOR_BUTTON:
            case VIDEO:
                return new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            case CAMERA:
                return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        }
        return null;
    }

    public boolean onRequestPermissionsResult(int requestCode, int[] grantResults) {
        IntentType type = IntentType.getByPermissionCode(requestCode);

        if (type == IntentType.UNKOWN) {
            return false;
        }

        if (!PermissionUtils.verifyPermissions(grantResults)) {
            callback.onPermissionFailed(type);
            return true;
        }

        switch (type) {
            case GALLERY:
                return true;
            case VIDEO_CURSOR_BUTTON:
            case VIDEO:
                captureVideo(type);
                return true;
            case CAMERA:
                captureImage();
                return true;
            default:
                return false;
        }
    }


    public enum IntentType {
        UNKOWN(-1, -1),
        GALLERY(9411, 8411),
        VIDEO(9412, 8412),
        VIDEO_CURSOR_BUTTON(9415, 8415),
        CAMERA(9413, 8413),
        FILE_SHARING(9414, 8414);

        public int requestCode;
        private int permissionCode;

        IntentType(int requestCode, int permissionCode) {
            this.requestCode = requestCode;
            this.permissionCode = permissionCode;
        }

        public static IntentType get(int requestCode) {

            if (requestCode == GALLERY.requestCode) {
                return GALLERY;
            }

            if (requestCode == CAMERA.requestCode) {
                return CAMERA;
            }

            if (requestCode == VIDEO.requestCode) {
                return VIDEO;
            }

            if (requestCode == VIDEO_CURSOR_BUTTON.requestCode) {
                return VIDEO_CURSOR_BUTTON;
            }

            if (requestCode == FILE_SHARING.requestCode) {
                return FILE_SHARING;
            }

            return UNKOWN;
        }


        public static IntentType getByPermissionCode(int permissionCode) {

            if (permissionCode == GALLERY.permissionCode) {
                return GALLERY;
            }

            if (permissionCode == CAMERA.permissionCode) {
                return CAMERA;
            }

            if (permissionCode == VIDEO.permissionCode) {
                return VIDEO;
            }

            if (permissionCode == VIDEO_CURSOR_BUTTON.permissionCode) {
                return VIDEO_CURSOR_BUTTON;
            }

            if (permissionCode == FILE_SHARING.permissionCode) {
                return FILE_SHARING;
            }

            return UNKOWN;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (pendingFileUri != null) {
            outState.putParcelable(SAVED_STATE_PENDING_URI, pendingFileUri);
        }
    }

    public interface Callback {
        void onDataReceived(IntentType type, Uri uri);

        void onCanceled(IntentType type);

        void onFailed(IntentType type);

        void openIntent(Intent intent, AssetIntentsManager.IntentType intentType);

        void onPermissionFailed(IntentType type);
    }
}
