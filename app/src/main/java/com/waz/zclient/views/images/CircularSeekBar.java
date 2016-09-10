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
/*
 * This part of the Wire software uses source code from the CircularSeekBar library.
 * (https://github.com/devadvance/circularseekbar)
 *
 * Copyright 2013 Matt Joseph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This custom view/widget was inspired and guided by:
 *
 * HoloCircleSeekBar - Copyright 2012 Jesus Manzano
 * HoloColorPicker - Copyright 2012 Lars Werkman (Designed by Marie Schweiz)
 *
 * Although I did not used the code from either project directly, they were both used as
 * reference material, and as a result, were extremely helpful.
 */
package com.waz.zclient.views.images;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import com.makeramen.roundedimageview.RoundedImageView;
import com.waz.zclient.R;
import com.waz.zclient.ui.utils.ColorUtils;
import com.waz.zclient.ui.utils.MathUtils;
import com.waz.zclient.views.OnDoubleClickListener;

public class CircularSeekBar extends RoundedImageView {

    // Default values
    private static final float DEFAULT_CIRCLE_X_RADIUS = 30f;
    private static final float DEFAULT_CIRCLE_Y_RADIUS = 30f;
    private static final float DEFAULT_POINTER_RADIUS = 7f;
    private static final float DEFAULT_POINTER_HALO_WIDTH = 6f;
    private static final float DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 5f;
    private static final float DEFAULT_START_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
    private static final float DEFAULT_END_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_CIRCLE_COLOR = Color.DKGRAY;

    public static final Property<CircularSeekBar, Float> DARKEN_LEVEL = new Property<CircularSeekBar, Float>(Float.class, "darkenLevel") {
        @Override
        public Float get(CircularSeekBar object) {
            return object.getDarkenLevel();
        }

        @Override
        public void set(CircularSeekBar object, Float value) {
            object.setDarkenLevel(value);
        }
    };
    /**
     * Holds the color value for {@code circlePaint} before the {@code Paint} instance is created.
     */
    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private static final int DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255);
    /**
     * Holds the color value for {@code circleProgressPaint} before the {@code Paint} instance is created.
     */
    private int circleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;
    private static final int DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255);
    /**
     * Holds the color value for {@code pointerPaint} before the {@code Paint} instance is created.
     */
    private int pointerColor = DEFAULT_POINTER_COLOR;
    private static final int DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255);
    /**
     * Holds the color value for {@code pointerHaloPaint} before the {@code Paint} instance is created.
     */
    private int pointerHaloColor = DEFAULT_POINTER_HALO_COLOR;
    private static final int DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255);
    /**
     * Holds the color value for {@code pointerHaloPaint} before the {@code Paint} instance is created.
     */
    private int pointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH;
    private static final int DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT;
    /**
     * Holds the color value for {@code circleFillPaint} before the {@code Paint} instance is created.
     */
    private int circleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
    private static final int DEFAULT_POINTER_ALPHA = 135;
    /**
     * Holds the alpha value for {@code pointerHaloPaint}.
     */
    private int pointerAlpha = DEFAULT_POINTER_ALPHA;
    private static final int DEFAULT_POINTER_ALPHA_ONTOUCH = 100;
    /**
     * Holds the OnTouch alpha value for {@code pointerHaloPaint}.
     */
    private int pointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;
    private static final boolean DEFAULT_USE_CUSTOM_RADII = false;
    private static final boolean DEFAULT_MAINTAIN_EQUAL_CIRCLE = true;
    private static final boolean DEFAULT_MOVE_OUTSIDE_CIRCLE = false;
    /**
     * Used to scale the dp units to pixels
     */
    private final float dpToPxScale = getResources().getDisplayMetrics().density;
    /**
     * Minimum touch target size in DP. 48dp is the Android design recommendation
     */
    private static final float MIN_TOUCH_TARGET_DP = 48;
    /**
     * {@code Paint} instance used to draw the inactive circle.
     */
    private Paint circlePaint;
    /**
     * {@code Paint} instance used to draw the circle fill.
     */
    private Paint circleFillPaint;
    /**
     * {@code Paint} instance used to draw the active circle (represents progress).
     */
    private Paint circleProgressPaint;
    /**
     * {@code Paint} instance used to draw the glow from the active circle.
     */
    private Paint circleProgressGlowPaint;
    /**
     * {@code Paint} instance used to draw the center of the pointer.
     * Note: This is broken on 4.0+, as BlurMasks do not work with hardware acceleration.
     */
    private Paint pointerPaint;
    /**
     * {@code Paint} instance used to draw the halo of the pointer.
     * Note: The halo is the part that changes transparency.
     */
    private Paint pointerHaloPaint;
    /**
     * {@code Paint} instance used to draw the border of the pointer, outside of the halo.
     */
    private Paint pointerHaloBorderPaint;
    /**
     * The width of the circle (in pixels).
     */
    private float circleStrokeWidth;
    /**
     * The X radius of the circle (in pixels).
     */
    private float circleXRadius;
    /**
     * The Y radius of the circle (in pixels).
     */
    private float circleYRadius;
    /**
     * The radius of the pointer (in pixels).
     */
    protected float pointerRadius;
    /**
     * The width of the pointer halo (in pixels).
     */
    private float pointerHaloWidth;
    /**
     * The width of the pointer halo border (in pixels).
     */
    private float pointerHaloBorderWidth;
    /**
     * Start angle of the CircularSeekBar.
     * Note: If startAngle and endAngle are set to the same angle, 0.1 is subtracted
     * from the endAngle to make the circle function properly.
     */
    private float startAngle;
    /**
     * End angle of the CircularSeekBar.
     * Note: If startAngle and endAngle are set to the same angle, 0.1 is subtracted
     * from the endAngle to make the circle function properly.
     */
    private float endAngle;
    /**
     * {@code RectF} that represents the circle (or ellipse) of the seekbar.
     */
    private RectF circleRectF = new RectF();
    /**
     * Distance (in degrees) that the the circle/semi-circle makes up.
     * This amount represents the max of the circle in degrees.
     */
    private float totalCircleDegrees;

    /**
     * Distance (in degrees) that the current progress makes up in the circle.
     */
    private float progressDegrees;

    /**
     * {@code Path} used to draw the circle/semi-circle.
     */
    protected Path circlePath;

    /**
     * {@code Path} used to draw the progress on the circle.
     */
    private Path circleProgressPath;

    /**
     * Max value that this CircularSeekBar is representing.
     */
    private int max;

    /**
     * Progress value that this CircularSeekBar is representing.
     */
    private int progress;

    /**
     * If true, then the user can specify the X and Y radii.
     * If false, then the View itself determines the size of the CircularSeekBar.
     */
    private boolean customRadii;

    /**
     * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.
     * The smaller of the two radii will always be used in this case.
     * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.
     */
    private boolean maintainEqualCircle;

    /**
     * Once a user has touched the circle, this determines if moving outside the circle is able
     * to change the position of the pointer (and in turn, the progress).
     */
    private boolean moveOutsideCircle;

    /**
     * Used for enabling/disabling the lock option for easier hitting of the 0 progress mark.
     */
    private boolean lockEnabled = true;

    /**
     * Used for when the user moves beyond the start of the circle when moving counter clockwise.
     * Makes it easier to hit the 0 progress mark.
     */
    private boolean lockAtStart = true;

    /**
     * Used for when the user moves beyond the end of the circle when moving clockwise.
     * Makes it easier to hit the 100% (max) progress mark.
     */
    private boolean lockAtEnd = false;

    /**
     * When the user is touching the circle on ACTION_DOWN, this is set to true.
     * Used when touching the CircularSeekBar.
     */
    private boolean userIsMovingPointer = false;

    /**
     * Represents the clockwise distance from {@code startAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private float cwDistanceFromStart;

    /**
     * Represents the counter-clockwise distance from {@code startAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private float ccwDistanceFromStart;

    /**
     * Represents the clockwise distance from {@code endAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private float cwDistanceFromEnd;

    /**
     * Represents the counter-clockwise distance from {@code endAngle} to the touch angle.
     * Used when touching the CircularSeekBar.
     * Currently unused, but kept just in case.
     */
    @SuppressWarnings("unused")
    private float ccwDistanceFromEnd;

    /**
     * The previous touch action value for {@code cwDistanceFromStart}.
     * Used when touching the CircularSeekBar.
     */
    private float lastCWDistanceFromStart;

    /**
     * Represents the clockwise distance from {@code pointerPosition} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private float cwDistanceFromPointer;

    /**
     * Represents the counter-clockwise distance from {@code pointerPosition} to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private float ccwDistanceFromPointer;

    /**
     * True if the user is moving clockwise around the circle, false if moving counter-clockwise.
     * Used when touching the CircularSeekBar.
     */
    private boolean isMovingCW;

    /**
     * The width of the circle used in the {@code RectF} that is used to draw it.
     * Based on either the View width or the custom X radius.
     */
    private float circleWidth;

    /**
     * The height of the circle used in the {@code RectF} that is used to draw it.
     * Based on either the View width or the custom Y radius.
     */
    private float circleHeight;

    /**
     * Represents the progress mark on the circle, in geometric degrees.
     * This is not provided by the user; it is calculated;
     */
    private float pointerPosition;

    /**
     * Pointer position in terms of X and Y coordinates.
     */
    protected float[] pointerPositionXY = new float[2];

    private GestureDetectorCompat gestureDetector;
    private OnDoubleClickListener onArtClickListener;

    /**
     * Listener.
     */
    private OnCircularSeekBarChangeListener onCircularSeekBarChangeListener;
    private boolean showPointer = false;
    private boolean enabled = true;
    private float darkenLevel;
    private OnLongClickListener onArtLongClickListener;

    public CircularSeekBar(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initialize the CircularSeekBar with the attributes from the XML style.
     * Uses the defaults defined at the top of this file when an attribute is not specified by the user.
     *
     * @param attrArray TypedArray containing the attributes.
     */
    protected void initAttributes(TypedArray attrArray) {
        circleXRadius = attrArray.getDimension(R.styleable.CircularSeekBar_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS * dpToPxScale);
        circleYRadius = attrArray.getDimension(R.styleable.CircularSeekBar_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS * dpToPxScale);
        pointerRadius = attrArray.getDimension(R.styleable.CircularSeekBar_pointer_radius, DEFAULT_POINTER_RADIUS * dpToPxScale);
        pointerHaloWidth = attrArray.getDimension(R.styleable.CircularSeekBar_pointer_halo_width, DEFAULT_POINTER_HALO_WIDTH * dpToPxScale);
        pointerHaloBorderWidth = attrArray.getDimension(R.styleable.CircularSeekBar_pointer_halo_border_width,
                                                        DEFAULT_POINTER_HALO_BORDER_WIDTH * dpToPxScale);
        circleStrokeWidth = attrArray.getDimension(R.styleable.CircularSeekBar_circle_stroke_width,
                                                   DEFAULT_CIRCLE_STROKE_WIDTH * dpToPxScale);

        String tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_color);
        if (tempColor != null) {
            try {
                pointerColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                pointerColor = DEFAULT_POINTER_COLOR;
            }
        }

        tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color);
        if (tempColor != null) {
            try {
                pointerHaloColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                pointerHaloColor = DEFAULT_POINTER_HALO_COLOR;
            }
        }

        tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color_ontouch);
        if (tempColor != null) {
            try {
                pointerHaloColorOnTouch = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                pointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH;
            }
        }

        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_color);
        if (tempColor != null) {
            try {
                circleColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                circleColor = DEFAULT_CIRCLE_COLOR;
            }
        }

        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_progress_color);
        if (tempColor != null) {
            try {
                circleProgressColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                circleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;
            }
        }

        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_fill);
        if (tempColor != null) {
            try {
                circleFillColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                circleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
            }
        }

        pointerAlpha = Color.alpha(pointerHaloColor);

        pointerAlphaOnTouch = attrArray.getInt(R.styleable.CircularSeekBar_pointer_alpha_ontouch, DEFAULT_POINTER_ALPHA_ONTOUCH);
        if (pointerAlphaOnTouch > 255 || pointerAlphaOnTouch < 0) {
            pointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;
        }

        max = attrArray.getInt(R.styleable.CircularSeekBar_max, DEFAULT_MAX);
        progress = attrArray.getInt(R.styleable.CircularSeekBar_progress, DEFAULT_PROGRESS);
        customRadii = attrArray.getBoolean(R.styleable.CircularSeekBar_use_custom_radii, DEFAULT_USE_CUSTOM_RADII);
        maintainEqualCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_maintain_equal_circle, DEFAULT_MAINTAIN_EQUAL_CIRCLE);
        moveOutsideCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE);

        // Modulo 360 right now to avoid constant conversion
        startAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_start_angle), DEFAULT_START_ANGLE) % 360f)) % 360f);
        endAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_end_angle), DEFAULT_END_ANGLE) % 360f)) % 360f);

        if (MathUtils.floatEqual(startAngle, endAngle)) {
            //startAngle = startAngle + 1f;
            endAngle = endAngle - .1f;
        }
        final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onArtClickListener != null) {
                    onArtClickListener.onSingleClick();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (onArtClickListener != null) {
                    onArtClickListener.onDoubleClick();
                }
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                if (onArtLongClickListener != null) {
                    onArtLongClickListener.onLongClick(CircularSeekBar.this);
                }
            }
        };
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        gestureDetector.setOnDoubleTapListener(gestureListener);
    }

    public void setOnArtClickListener(OnDoubleClickListener onArtClickListener) {
        this.onArtClickListener = onArtClickListener;
    }

    public void setOnArtLongClickListener(View.OnLongClickListener onArtLongClickListener) {
        this.onArtLongClickListener = onArtLongClickListener;
    }

    public boolean isShowPointer() {
        return showPointer;
    }

    public void setShowPointer(boolean showPointer) {
        this.showPointer = showPointer;
        requestLayout();
        invalidate();
    }

    /**
     * Initializes the {@code Paint} objects with the appropriate styles.
     */
    protected void initPaints() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(circleStrokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.ROUND);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);

        circleFillPaint = new Paint();
        circleFillPaint.setAntiAlias(true);
        circleFillPaint.setDither(true);
        circleFillPaint.setColor(circleFillColor);
        circleFillPaint.setStyle(Paint.Style.FILL);

        circleProgressPaint = new Paint();
        circleProgressPaint.setAntiAlias(true);
        circleProgressPaint.setDither(true);
        circleProgressPaint.setColor(circleProgressColor);
        circleProgressPaint.setStrokeWidth(circleStrokeWidth);
        circleProgressPaint.setStyle(Paint.Style.STROKE);
        circleProgressGlowPaint = new Paint();
        circleProgressGlowPaint.set(circleProgressPaint);
        if (!isInEditMode()) {
            circleProgressGlowPaint.setMaskFilter(new BlurMaskFilter((5f * dpToPxScale), BlurMaskFilter.Blur.NORMAL));
        }

        pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setDither(true);
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setColor(pointerColor);
        pointerPaint.setStrokeWidth(pointerRadius);

        pointerHaloPaint = new Paint();
        pointerHaloPaint.set(pointerPaint);
        pointerHaloPaint.setColor(pointerHaloColor);
        pointerHaloPaint.setAlpha(pointerAlpha);
        pointerHaloPaint.setStrokeWidth(pointerRadius + pointerHaloWidth);

        pointerHaloBorderPaint = new Paint();
        pointerHaloBorderPaint.set(pointerPaint);
        pointerHaloBorderPaint.setStrokeWidth(pointerHaloBorderWidth);
        pointerHaloBorderPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Calculates the total degrees between startAngle and endAngle, and sets totalCircleDegrees
     * to this value.
     */
    private void calculateTotalDegrees() {
        totalCircleDegrees = (360f - (startAngle - endAngle)) % 360f; // Length of the entire circle/arc
        if (totalCircleDegrees <= 0f) {
            totalCircleDegrees = 360f;
        }
    }

    /**
     * Calculate the degrees that the progress represents. Also called the sweep angle.
     * Sets progressDegrees to that value.
     */
    private void calculateProgressDegrees() {
        progressDegrees = pointerPosition - startAngle; // Verified
        progressDegrees = (progressDegrees < 0 ? 360f + progressDegrees : progressDegrees); // Verified
    }

    /**
     * Calculate the pointer position (and the end of the progress arc) in degrees.
     * Sets pointerPosition to that value.
     */
    private void calculatePointerAngle() {
        float progressPercent = ((float) progress / (float) max);
        pointerPosition = (progressPercent * totalCircleDegrees) + startAngle;
        pointerPosition = pointerPosition % 360f;
    }

    private void calculatePointerXYPosition() {
        PathMeasure pm = new PathMeasure(circleProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), pointerPositionXY, null);
        if (!returnValue) {
            pm = new PathMeasure(circlePath, false);
            returnValue = pm.getPosTan(0, pointerPositionXY, null);
        }
    }

    /**
     * Initialize the {@code Path} objects with the appropriate values.
     */
    private void initPaths() {
        circlePath = new Path();
        circlePath.addArc(circleRectF, startAngle, totalCircleDegrees);

        circleProgressPath = new Path();
        circleProgressPath.addArc(circleRectF, startAngle, progressDegrees);
    }

    /**
     * Initialize the {@code RectF} objects with the appropriate values.
     */
    private void initRects() {
        circleRectF.set(-circleWidth, -circleHeight, circleWidth, circleHeight);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(this.getWidth() / 2f, this.getHeight() / 2f);

        canvas.drawPath(circlePath, circlePaint);
        canvas.drawPath(circleProgressPath, circleProgressGlowPaint);
        canvas.drawPath(circleProgressPath, circleProgressPaint);

        canvas.drawPath(circlePath, circleFillPaint);

        if (showPointer) {
            canvas.drawCircle(pointerPositionXY[0], pointerPositionXY[1], pointerRadius + pointerHaloWidth,
                              pointerHaloPaint);
            canvas.drawCircle(pointerPositionXY[0], pointerPositionXY[1], pointerRadius, pointerPaint);
            if (userIsMovingPointer) {
                canvas.drawCircle(pointerPositionXY[0], pointerPositionXY[1], pointerRadius + pointerHaloWidth + (pointerHaloBorderWidth / 2f),
                                  pointerHaloBorderPaint);
            }
        }
    }

    /**
     * Get the progress of the CircularSeekBar.
     *
     * @return The progress of the CircularSeekBar.
     */
    public int getProgress() {
        return Math.round((float) max * progressDegrees / totalCircleDegrees);
    }

    /**
     * Set the progress of the CircularSeekBar.
     * If the progress is the same, then any listener will not receive a onProgressChanged event.
     *
     * @param progress The progress to set the CircularSeekBar to.
     */
    public void setProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            if (onCircularSeekBarChangeListener != null) {
                onCircularSeekBarChangeListener.onProgressChanged(this, progress, false);
            }

            recalculateAll();
            invalidate();
        }
    }

    private void setProgressBasedOnAngle(float angle) {
        pointerPosition = angle;
        calculateProgressDegrees();
        progress = Math.round((float) max * progressDegrees / totalCircleDegrees);
    }

    private void recalculateAll() {
        calculateTotalDegrees();
        calculatePointerAngle();
        calculateProgressDegrees();

        initRects();

        initPaths();

        calculatePointerXYPosition();

        setCornerRadius(circleRectF.width() / 2f + circleStrokeWidth);
    }

    public void setProgressEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        if (maintainEqualCircle) {
            int min = Math.min(width, height);
            if (min == 0) {
                min = Math.max(width, height);
            }
            height = min;
            width = min;
            setMeasuredDimension(min, min);
            super.setMeasuredDimension(min, min);
        } else {
            setMeasuredDimension(width, height);
            super.setMeasuredDimension(width, height);
        }

        // Set the circle width and height based on the view for the moment
        circleHeight = (float) height / 2f - circleStrokeWidth / 2f;
        if (showPointer) {
            circleHeight -= pointerRadius - (pointerHaloBorderWidth * 1.5f);
        }
        circleWidth = (float) width / 2f - circleStrokeWidth / 2f;
        if (showPointer) {
            circleWidth -= pointerRadius - (pointerHaloBorderWidth * 1.5f);
        }

        // If it is not set to use custom
        if (customRadii) {
            // Check to make sure the custom radii are not out of the view. If they are, just use the view values
            if ((circleYRadius - circleStrokeWidth - pointerRadius - pointerHaloBorderWidth) < circleHeight) {
                circleHeight = circleYRadius - circleStrokeWidth - pointerRadius - (pointerHaloBorderWidth * 1.5f);
            }

            if ((circleXRadius - circleStrokeWidth - pointerRadius - pointerHaloBorderWidth) < circleWidth) {
                circleWidth = circleXRadius - circleStrokeWidth - pointerRadius - (pointerHaloBorderWidth * 1.5f);
            }
        }

        if (maintainEqualCircle) { // Applies regardless of how the values were determined
            float min = Math.min(circleHeight, circleWidth);
            circleHeight = min;
            circleWidth = min;
        }

        recalculateAll();
    }

    public boolean isLockEnabled() {
        return lockEnabled;
    }

    public void setLockEnabled(boolean lockEnabled) {
        this.lockEnabled = lockEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Convert coordinates to our internal coordinate system
        final float x = event.getX() - getWidth() / 2;
        final float y = event.getY() - getHeight() / 2;

        // Get the distance from the center of the circle in terms of x and y
        final float distanceX = circleRectF.centerX() - x;
        final float distanceY = circleRectF.centerY() - y;

        // Get the distance from the center of the circle in terms of a radius
        final float touchEventRadius = (float) Math.sqrt((Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
        final float minimumTouchTarget = MIN_TOUCH_TARGET_DP * dpToPxScale; // Convert minimum touch target into px
        final float additionalRadius; // Either uses the minimumTouchTarget size or larger if the ring/pointer is larger

        if (circleStrokeWidth < minimumTouchTarget) { // If the width is less than the minimumTouchTarget, use the minimumTouchTarget
            additionalRadius = minimumTouchTarget / 2;
        } else {
            additionalRadius = circleStrokeWidth / 2; // Otherwise use the width
        }

        final float innerRadius = Math.min(circleHeight, circleWidth) - additionalRadius; // Min inner radius of the circle, including the minimumTouchTarget or wheel width
        if (touchEventRadius <= innerRadius) {
            gestureDetector.onTouchEvent(event);
            userIsMovingPointer = false;
            return true;
        }

        if (!isEnabled() || !enabled) {
            return super.onTouchEvent(event);
        }


        final float outerRadius = Math.max(circleHeight, circleWidth) + additionalRadius; // Max outer radius of the circle, including the minimumTouchTarget or wheel width
        float touchAngle;
        touchAngle = (float) ((java.lang.Math.atan2(y, x) / Math.PI * 180) % 360); // Verified
        touchAngle = (touchAngle < 0 ? 360 + touchAngle : touchAngle); // Verified

        cwDistanceFromStart = touchAngle - startAngle; // Verified
        cwDistanceFromStart = (cwDistanceFromStart < 0 ? 360f + cwDistanceFromStart : cwDistanceFromStart); // Verified
        ccwDistanceFromStart = 360f - cwDistanceFromStart; // Verified

        cwDistanceFromEnd = touchAngle - endAngle; // Verified
        cwDistanceFromEnd = (cwDistanceFromEnd < 0 ? 360f + cwDistanceFromEnd : cwDistanceFromEnd); // Verified
        ccwDistanceFromEnd = 360f - cwDistanceFromEnd; // Verified

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // These are only used for ACTION_DOWN for handling if the pointer was the part that was touched
                float pointerRadiusDegrees = (float) ((pointerRadius * 180) / (Math.PI * Math.max(circleHeight,
                                                                                                  circleWidth)));
                cwDistanceFromPointer = touchAngle - pointerPosition;
                cwDistanceFromPointer = (cwDistanceFromPointer < 0 ? 360f + cwDistanceFromPointer : cwDistanceFromPointer);
                ccwDistanceFromPointer = 360f - cwDistanceFromPointer;
                // This is for if the first touch is on the actual pointer.
                if (((touchEventRadius >= innerRadius) && (touchEventRadius <= outerRadius)) &&
                    ((cwDistanceFromPointer <= pointerRadiusDegrees) || (ccwDistanceFromPointer <= pointerRadiusDegrees))) {
                    setProgressBasedOnAngle(pointerPosition);
                    lastCWDistanceFromStart = cwDistanceFromStart;
                    isMovingCW = true;
                    pointerHaloPaint.setAlpha(pointerAlphaOnTouch);
                    pointerHaloPaint.setColor(pointerHaloColorOnTouch);
                    recalculateAll();
                    invalidate();
                    if (onCircularSeekBarChangeListener != null) {
                        onCircularSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                    userIsMovingPointer = true;
                    lockAtEnd = false;
                    lockAtStart = false;
                } else if (cwDistanceFromStart > totalCircleDegrees) { // If the user is touching outside of the start AND end
                    userIsMovingPointer = false;
                    return false;
                } else if ((touchEventRadius >= innerRadius) &&
                           (touchEventRadius <= outerRadius)) { // If the user is touching near the circle
                    setProgressBasedOnAngle(touchAngle);
                    lastCWDistanceFromStart = cwDistanceFromStart;
                    isMovingCW = true;
                    pointerHaloPaint.setAlpha(pointerAlphaOnTouch);
                    pointerHaloPaint.setColor(pointerHaloColorOnTouch);
                    recalculateAll();
                    invalidate();
                    if (onCircularSeekBarChangeListener != null) {
                        onCircularSeekBarChangeListener.onStartTrackingTouch(this);
                        onCircularSeekBarChangeListener.onProgressChanged(this, progress, true);
                    }
                    userIsMovingPointer = true;
                    lockAtEnd = false;
                    lockAtStart = false;
                } else { // If the user is not touching near the circle
                    userIsMovingPointer = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (userIsMovingPointer) {
                    if (lastCWDistanceFromStart < cwDistanceFromStart) {
                        if ((cwDistanceFromStart - lastCWDistanceFromStart) > 180f && !isMovingCW) {
                            lockAtStart = true;
                            lockAtEnd = false;
                        } else {
                            isMovingCW = true;
                        }
                    } else {
                        if ((lastCWDistanceFromStart - cwDistanceFromStart) > 180f && isMovingCW) {
                            lockAtEnd = true;
                            lockAtStart = false;
                        } else {
                            isMovingCW = false;
                        }
                    }

                    if (lockAtStart && isMovingCW) {
                        lockAtStart = false;
                    }
                    if (lockAtEnd && !isMovingCW) {
                        lockAtEnd = false;
                    }
                    if (lockAtStart && !isMovingCW && (ccwDistanceFromStart > 90)) {
                        lockAtStart = false;
                    }
                    if (lockAtEnd && isMovingCW && (cwDistanceFromEnd > 90)) {
                        lockAtEnd = false;
                    }
                    // Fix for passing the end of a semi-circle quickly
                    if (!lockAtEnd && cwDistanceFromStart > totalCircleDegrees && isMovingCW && lastCWDistanceFromStart < totalCircleDegrees) {
                        lockAtEnd = true;
                    }

                    if (lockAtStart && lockEnabled) {
                        // TODO: Add a check if progress is already 0, in which case don't call the listener
                        progress = 0;
                        recalculateAll();
                        invalidate();
                        if (onCircularSeekBarChangeListener != null) {
                            onCircularSeekBarChangeListener.onProgressChanged(this, progress, true);
                        }

                    } else if (lockAtEnd && lockEnabled) {
                        progress = max;
                        recalculateAll();
                        invalidate();
                        if (onCircularSeekBarChangeListener != null) {
                            onCircularSeekBarChangeListener.onProgressChanged(this, progress, true);
                        }
                    } else if ((moveOutsideCircle) || (touchEventRadius <= outerRadius)) {
                        if (!(cwDistanceFromStart > totalCircleDegrees)) {
                            setProgressBasedOnAngle(touchAngle);
                        }
                        recalculateAll();
                        invalidate();
                        if (onCircularSeekBarChangeListener != null) {
                            onCircularSeekBarChangeListener.onProgressChanged(this, progress, true);
                        }
                    } else {
                        break;
                    }

                    lastCWDistanceFromStart = cwDistanceFromStart;
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                pointerHaloPaint.setAlpha(pointerAlpha);
                pointerHaloPaint.setColor(pointerHaloColor);
                if (userIsMovingPointer) {
                    userIsMovingPointer = false;
                    invalidate();
                    if (onCircularSeekBarChangeListener != null) {
                        onCircularSeekBarChangeListener.onStopTrackingTouch(this);
                    }
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL: // Used when the parent view intercepts touches for things like scrolling
                pointerHaloPaint.setAlpha(pointerAlpha);
                pointerHaloPaint.setColor(pointerHaloColor);
                userIsMovingPointer = false;
                invalidate();
                break;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        return true;
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, 0);

        initAttributes(attrArray);

        attrArray.recycle();

        initPaints();

        initInnerImage();
    }

    private void initInnerImage() {

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable("PARENT", superState);
        state.putInt("MAX", max);
        state.putInt("PROGRESS", progress);
        state.putInt("circleColor", circleColor);
        state.putInt("circleProgressColor", circleProgressColor);
        state.putInt("pointerColor", pointerColor);
        state.putInt("pointerHaloColor", pointerHaloColor);
        state.putInt("pointerHaloColorOnTouch", pointerHaloColorOnTouch);
        state.putInt("pointerAlpha", pointerAlpha);
        state.putInt("pointerAlphaOnTouch", pointerAlphaOnTouch);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;

        Parcelable superState = savedState.getParcelable("PARENT");
        super.onRestoreInstanceState(superState);

        max = savedState.getInt("MAX");
        progress = savedState.getInt("PROGRESS");
        circleColor = savedState.getInt("circleColor");
        circleProgressColor = savedState.getInt("circleProgressColor");
        pointerColor = savedState.getInt("pointerColor");
        pointerHaloColor = savedState.getInt("pointerHaloColor");
        pointerHaloColorOnTouch = savedState.getInt("pointerHaloColorOnTouch");
        pointerAlpha = savedState.getInt("pointerAlpha");
        pointerAlphaOnTouch = savedState.getInt("pointerAlphaOnTouch");

        initPaints();

        recalculateAll();
    }


    public void setOnSeekBarChangeListener(OnCircularSeekBarChangeListener l) {
        onCircularSeekBarChangeListener = l;
    }

    public void setCircleStrokeWidth(float circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth;
        circlePaint.setStrokeWidth(circleStrokeWidth);
        circleProgressPaint.setStrokeWidth(circleStrokeWidth);
        requestLayout();
        invalidate();
    }

    /**
     * Gets the circle color.
     *
     * @return An integer color value for the circle
     */
    public int getCircleColor() {
        return circleColor;
    }

    /**
     * Sets the circle color.
     *
     * @param color the color of the circle
     */
    public void setCircleColor(int color) {
        circleColor = color;
        circlePaint.setColor(circleColor);
        invalidate();
    }

    /**
     * Gets the circle progress color.
     *
     * @return An integer color value for the circle progress
     */
    public int getCircleProgressColor() {
        return circleProgressColor;
    }

    /**
     * Sets the circle progress color.
     *
     * @param color the color of the circle progress
     */
    public void setCircleProgressColor(int color) {
        circleProgressColor = color;
        circleProgressPaint.setColor(circleProgressColor);
        invalidate();
    }

    /**
     * Gets the pointer color.
     *
     * @return An integer color value for the pointer
     */
    public int getPointerColor() {
        return pointerColor;
    }

    /**
     * Sets the pointer color.
     *
     * @param color the color of the pointer
     */
    public void setPointerColor(int color) {
        pointerColor = color;
        pointerPaint.setColor(pointerColor);
        invalidate();
    }

    /**
     * Gets the pointer halo color.
     *
     * @return An integer color value for the pointer halo
     */
    public int getPointerHaloColor() {
        return pointerHaloColor;
    }

    /**
     * Sets the pointer halo color.
     *
     * @param color the color of the pointer halo
     */
    public void setPointerHaloColor(int color) {
        pointerHaloColor = color;
        pointerHaloPaint.setColor(pointerHaloColor);
        invalidate();
    }

    /**
     * Gets the pointer alpha value.
     *
     * @return An integer alpha value for the pointer (0..255)
     */
    public int getPointerAlpha() {
        return pointerAlpha;
    }

    /**
     * Sets the pointer alpha.
     *
     * @param alpha the alpha of the pointer
     */
    public void setPointerAlpha(int alpha) {
        if (alpha >= 0 && alpha <= 255) {
            pointerAlpha = alpha;
            pointerHaloPaint.setAlpha(pointerAlpha);
            invalidate();
        }
    }

    /**
     * Gets the pointer alpha value when touched.
     *
     * @return An integer alpha value for the pointer (0..255) when touched
     */
    public int getPointerAlphaOnTouch() {
        return pointerAlphaOnTouch;
    }

    /**
     * Sets the pointer alpha when touched.
     *
     * @param alpha the alpha of the pointer (0..255) when touched
     */
    public void setPointerAlphaOnTouch(int alpha) {
        if (alpha >= 0 && alpha <= 255) {
            pointerAlphaOnTouch = alpha;
        }
    }

    /**
     * Gets the circle fill color.
     *
     * @return An integer color value for the circle fill
     */
    public int getCircleFillColor() {
        return circleFillColor;
    }

    /**
     * Sets the circle fill color.
     *
     * @param color the color of the circle fill
     */
    public void setCircleFillColor(int color) {
        circleFillColor = color;
        circleFillPaint.setColor(circleFillColor);
        invalidate();
    }

    /**
     * Get the current max of the CircularSeekBar.
     *
     * @return Synchronized integer value of the max.
     */
    public synchronized int getMax() {
        return max;
    }

    /**
     * Set the max of the CircularSeekBar.
     * If the new max is less than the current progress, then the progress will be set to zero.
     * If the progress is changed as a result, then any listener will receive a onProgressChanged event.
     *
     * @param max The new max for the CircularSeekBar.
     */
    public void setMax(int max) {
        if (!(max <= 0)) { // Check to make sure it's greater than zero
            if (max <= progress) {
                progress = 0; // If the new max is less than current progress, set progress to zero
                if (onCircularSeekBarChangeListener != null) {
                    onCircularSeekBarChangeListener.onProgressChanged(this, progress, false);
                }
            }
            this.max = max;

            recalculateAll();
            invalidate();
        }
    }

    public void setDarkenLevel(float darkenLevel) {
        this.darkenLevel = darkenLevel;
        if (darkenLevel < 0f || darkenLevel > 1f) {
            throw new IllegalArgumentException("darkenLevel must be between 0 and 1");
        }
        super.setColorFilter(ColorUtils.injectAlpha(darkenLevel, Color.BLACK), PorterDuff.Mode.DARKEN);
    }

    public float getDarkenLevel() {
        return darkenLevel;
    }

    /**
     * Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.
     */
    public interface OnCircularSeekBarChangeListener {

        void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser);

        void onStopTrackingTouch(CircularSeekBar seekBar);

        void onStartTrackingTouch(CircularSeekBar seekBar);
    }

}
