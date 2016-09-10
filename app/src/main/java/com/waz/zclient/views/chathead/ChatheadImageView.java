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
package com.waz.zclient.views.chathead;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.waz.api.ImageAsset;
import com.waz.api.LoadHandle;
import com.waz.api.UpdateListener;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.ui.utils.ResourceUtils;
import com.waz.zclient.ui.utils.TypefaceUtils;
import com.waz.zclient.ui.text.TypefaceTextView;
import timber.log.Timber;

public class ChatheadImageView extends FrameLayout implements UpdateListener {

    public static final String TAG = ChatheadImageView.class.getName();
    private static final int DEFAULT_COLOR = Color.TRANSPARENT;

    private enum ChatHeadState {
        IDLE,
        ERROR,
        BITMAP_NOT_LOADED_YET,
        BITMAP_LOADED
    }

    private ImageView imageView;
    private TypefaceTextView typefaceTextView;

    private ImageAsset imageAsset;
    private LoadHandle handle;
    private User user;

    private Paint paint;

    private ChatHeadState chatHeadState;

    public ChatheadImageView(Context context) {
        this(context, null);
    }

    public ChatheadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatheadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setUser(User user) {
        // user is empty
        if (user == null) {
            removeOldUser();
            setState(ChatHeadState.ERROR);
            return;
        }

        // same user
        if (this.user != null && this.user.getId().equals(user.getId())) {
            updated();
            return;
        }

        removeOldUser();

        // connect new user
        this.user = user;
        this.user.addUpdateListener(this);
        imageAsset = this.user.getPicture();
        if (imageAsset != null) {
            imageAsset.addUpdateListener(this);
        }

        setState(ChatHeadState.BITMAP_NOT_LOADED_YET);
        updated();
    }

    public User getUser() {
        return user;
    }

    private void removeOldUser() {
        if (user != null) {
            user.removeUpdateListener(this);
            user = null;
        }

        if (imageAsset != null) {
            imageAsset.removeUpdateListener(this);
            imageAsset = null;
        }

        if (handle != null) {
            handle.cancel();
            handle = null;
        }

        imageView.setImageBitmap(null);
    }

    private void setState(ChatHeadState chatHeadState) {
        switch (chatHeadState) {
            case IDLE:
                break;
            case ERROR:
                paint.setColor(DEFAULT_COLOR);
                break;
            case BITMAP_NOT_LOADED_YET:
                typefaceTextView.setText(user.getInitials());
                paint.setColor(user.getAccent().getColor());
                break;
            case BITMAP_LOADED:
                typefaceTextView.setText("");
                paint.setColor(user.getAccent().getColor());
                break;
        }

        this.chatHeadState = chatHeadState;
    }

    private void init() {
        chatHeadState = ChatHeadState.IDLE;
        imageView = new ImageView(getContext());
        typefaceTextView = new TypefaceTextView(getContext());
        addView(imageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(typefaceTextView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initTextView() {
        int fontColor = getResources().getColor(R.color.chathead__user_initials__font_color);
        Typeface initialsTypeface = TypefaceUtils.getTypeface(getResources().getString(R.string.chathead__user_initials__font));
        int fontSize = (int) (getMeasuredWidth() *
                             ResourceUtils.getResourceFloat(getResources(), R.dimen.notifications__incoming_call__chathead__font_proportion));

        typefaceTextView.setTextColor(fontColor);
        typefaceTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        typefaceTextView.setTypeface(initialsTypeface);
        typefaceTextView.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updated();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        switch (chatHeadState) {
            case ERROR:
            case BITMAP_NOT_LOADED_YET:
            case BITMAP_LOADED:
                canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2, paint);
                break;
        }
        super.dispatchDraw(canvas);
    }

    @Override
    public void updated() {
        initTextView();

        setState(chatHeadState);
        if (imageAsset != null) {
            imageAsset.removeUpdateListener(this);
            imageAsset = null;
        }

        if (user != null) {
            imageAsset = user.getPicture();
        }
        if (imageAsset == null) {
            return;
        }
        imageAsset.addUpdateListener(this);

        if (handle != null) {
            handle.cancel();
        }

        handle = imageAsset.getRoundBitmap(imageView.getMeasuredWidth(), 0, DEFAULT_COLOR, new ImageAsset.BitmapCallback() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, boolean b) {
                setState(ChatHeadState.BITMAP_LOADED);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapLoadingFailed() {
                Timber.e("Loading of bitmap in failed");
            }
        });
    }

}
