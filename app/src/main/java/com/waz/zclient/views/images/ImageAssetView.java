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
package com.waz.zclient.views.images;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.waz.api.ImageAsset;
import com.waz.api.LoadHandle;
import com.waz.api.UpdateListener;
import com.waz.zclient.R;
import timber.log.Timber;

public class ImageAssetView extends ImageView implements UpdateListener {

    private BitmapLoadedCallback bitmapLoadedCallback;
    private LoadHandle bitmapLoadHandle;
    private boolean shouldScaleForPortraitMode;
    private ImageAsset imageAsset;
    private boolean showPreview;
    private boolean animateFirstTimeBitmapAppearance;

    private ObjectAnimator alphaAnimator;

    private final ImageAsset.BitmapCallback loadCallback = new ImageAsset.BitmapCallback() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, boolean isPreview) {
            Timber.i("ImageAssetView %s :: onBitmapLoaded(), id=%s preview=%b, w=%d, h=%d",
                     ImageAssetView.this,
                     imageAsset.getId(),
                     isPreview,
                     bitmap.getWidth(),
                     bitmap.getHeight());
            if (isPreview && !showPreview) {
                return;
            }
            setScalePolicy(bitmap);
            setImageBitmap(bitmap);
            if (bitmapLoadedCallback != null && !isPreview) {
                bitmapLoadedCallback.onBitmapLoadFinished(true);
            }

            if (!animateFirstTimeBitmapAppearance) {
                return;
            }
            animateFirstTimeBitmapAppearance = false;
            alphaAnimator = ObjectAnimator.ofFloat(ImageAssetView.this, View.ALPHA, 0, 1f);
            alphaAnimator.setDuration(getResources().getInteger(R.integer.animation_duration_medium));
            alphaAnimator.start();
        }

        @Override
        public void onBitmapLoadingFailed() {
            logBitmapLoadError();
            clearImage();
            if (bitmapLoadedCallback != null) {
                bitmapLoadedCallback.onBitmapLoadFinished(false);
            }
        }
    };

    public ImageAssetView(Context context) {
        super(context);
    }

    public ImageAssetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageAssetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageAsset(@Nullable final ImageAsset imageAsset) {
        clearImage();
        animateFirstTimeBitmapAppearance = true;
        bindImageAsset(imageAsset);
    }

    public void setShouldScaleForPortraitMode(boolean shouldScaleForPortraitMode) {
        this.shouldScaleForPortraitMode = shouldScaleForPortraitMode;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public void clearImage() {
        unbindImageAsset();
        imageAsset = null;
        super.setImageDrawable(null);
    }

    public void setBitmapLoadedCallback(BitmapLoadedCallback bitmapLoadedCallback) {
        this.bitmapLoadedCallback = bitmapLoadedCallback;
    }

    @Override
    public void updated() {
        loadBitmap();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbindImageAsset();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (imageAsset != null) {
            imageAsset.addUpdateListener(this);
            loadBitmap();
        }
    }

    private void bindImageAsset(ImageAsset imageAsset) {
        this.imageAsset = imageAsset;
        if (imageAsset != null) {
            Timber.i("Binding ImageAssetView %s to ImageAsset %s", this, imageAsset.getId());
            this.imageAsset.addUpdateListener(this);
            loadBitmap();
        }
    }

    private void unbindImageAsset() {
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
        }

        cancelPreviousBitmapLoad();
        if (imageAsset != null) {
            Timber.i("Unbinding ImageAssetView %s from ImageAsset %s", this, imageAsset.getId());
            imageAsset.removeUpdateListener(this);
        }
    }

    private void loadBitmap() {
        cancelPreviousBitmapLoad();

        if (imageAsset.isEmpty()) {
            Timber.i("ImageAssetView %s :: loadBitmap() empty ImageAsset", this);
            if (bitmapLoadedCallback != null) {
                bitmapLoadedCallback.onBitmapLoadFinished(false);
            }
            return;
        }
        Timber.i("ImageAssetView %s :: loadBitmap(), id=%s", this, imageAsset.getId());

        bitmapLoadHandle = imageAsset.getBitmap(getWidth(), loadCallback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed && imageAsset != null) {
            loadBitmap(); //request bitmap again since view size has changed
        }
    }

    private void setScalePolicy(Bitmap bitmap) {
        if (shouldScaleForPortraitMode) {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                setScaleType(ScaleType.FIT_CENTER);
            } else {
                setScaleType(ScaleType.CENTER_CROP);
            }
        }
    }

    private void logBitmapLoadError() {
        Timber.e("ImageAssetView %s :: failed loading bitmap for %s id=%s",
                 this,
                 imageAsset,
                 imageAsset != null ? imageAsset.getId() : null);
    }

    private void cancelPreviousBitmapLoad() {
        if (bitmapLoadHandle != null) {
            bitmapLoadHandle.cancel();
        }
    }

    public interface BitmapLoadedCallback {
        void onBitmapLoadFinished(boolean bitmapLoaded);
    }
}
