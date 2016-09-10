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
package com.waz.zclient.pages.main.conversation.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.waz.api.User;
import com.waz.api.UsersList;
import com.waz.zclient.R;
import com.waz.zclient.ui.animation.interpolators.penner.Expo;
import com.waz.zclient.ui.animation.interpolators.penner.Quart;
import com.waz.zclient.ui.cursor.TypingIndicator;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.views.chathead.ChatheadImageView;
import com.waz.zclient.common.views.ChatheadView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TypingIndicatorView extends FrameLayout implements TypingIndicator {

    private static final float CHATHEAD_ANIMATION_INITIAL_SCALE = 0.83f;
    private ValueAnimator chatheadBounceAnimator;
    private Map<String, User> typingUsers;
    private ChatheadView chathead;
    private ChatheadImageView selfChatheadView;
    private boolean fadeInOnNextShow;

    public TypingIndicatorView(Context context) {
        this(context, null);
    }

    public TypingIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypingIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.typing_indicator, this, true);

        typingUsers = new HashMap<>();
        chathead = ViewUtils.getView(this, R.id.cv__chathead);
        chathead.setAlpha(0f);

        selfChatheadView = ViewUtils.getView(this, R.id.civ__cursor__self_avatar);
        selfChatheadView.setVisibility(GONE);
    }

    public void usersUpdated(UsersList usersList, boolean allowShow) {
        addNewTypingUsers(usersList, allowShow);
        removeNoLongerTypingUsers(usersList);
    }

    public void reset() {
        typingUsers.clear();
        fadeInOnNextShow = false;
        hide();
    }

    public void setSelfUser(User user) {
        selfChatheadView.setUser(user);
    }

    private void addNewTypingUsers(@Nullable UsersList usersList, boolean allowShow) {
        // This will only show the chathead and name of the last user is usersList. For multiple users, a new ChatheadView
        // and TypefaceTextView instances should be created and added to the layout.
        final int userCount = usersList != null ? usersList.size()
                                                : 0;

        for (int i = 0; i < userCount; i++) {
            User user = usersList.get(i);
            if (typingUsers.containsKey(user.getId())) {
                continue;
            }
            chathead.setScaleX(CHATHEAD_ANIMATION_INITIAL_SCALE);
            chathead.setScaleY(CHATHEAD_ANIMATION_INITIAL_SCALE);
            chathead.setUser(user);
            chathead.setTag(user.getId());

            typingUsers.put(user.getId(), user);
        }
        if (userCount > 0) {
            if (allowShow) {
                startAnimation();
            } else {
                // If we were scrolled away from the bottom, the chathead appearance animation is just a fade,
                // without a translation. This flag keeps track of which case it should be when startAnimation() is called.
                fadeInOnNextShow = true;
            }
        } else {
            fadeInOnNextShow = false;
        }
    }

    private void removeNoLongerTypingUsers(@Nullable UsersList usersList) {
        if (usersList == null) {
            typingUsers.clear();
            return;
        }

        Iterator<String> iterator = typingUsers.keySet().iterator();
        while (iterator.hasNext()) {
            String userId = iterator.next();
            boolean stillDisplayed = false;
            for (int i = 0; i < usersList.size(); i++) {
                if (usersList.get(i).getId().equals(userId)) {
                    stillDisplayed = true;
                }
            }
            if (!stillDisplayed) {
                // if supporting multiple users, remove views of no-longer-typing users here
                iterator.remove();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onVisibilityHasChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        onVisibilityHasChanged();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        onVisibilityHasChanged();
    }

    private void onVisibilityHasChanged() {
        int visibility = getVisibility();
        if (visibility == View.GONE ||
            visibility == View.INVISIBLE) {
            stopAnimation();
        }
    }

    private void startAnimation() {
        if (chatheadBounceAnimator != null &&
            chatheadBounceAnimator.isStarted()) {
            return;
        }

        if (fadeInOnNextShow) {
            ViewUtils.fadeInView(chathead);
        } else {
            chathead.setTranslationY(getResources().getDimensionPixelSize(R.dimen.typing_indicator__chathead_translation));
            chathead.animate()
                    .alpha(1f)
                    .translationYBy(-getResources().getDimensionPixelSize(R.dimen.typing_indicator__chathead_translation))
                    .setDuration(getResources().getInteger(R.integer.framework_animation_duration_long))
                    .setInterpolator(new Expo.EaseOut())
                    .start();
            fadeInOnNextShow = true;
        }

        chatheadBounceAnimator = ObjectAnimator.ofFloat(CHATHEAD_ANIMATION_INITIAL_SCALE, 1f);
        chatheadBounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                chathead.setScaleX((Float) animation.getAnimatedValue());
                chathead.setScaleY((Float) animation.getAnimatedValue());
            }
        });
        chatheadBounceAnimator.setRepeatCount(3);
        chatheadBounceAnimator.setDuration(getResources().getInteger(R.integer.typing_indicator__pulse_duration));
        chatheadBounceAnimator.setRepeatMode(ValueAnimator.REVERSE);
        chatheadBounceAnimator.setInterpolator(new Expo.EaseIn());
        chatheadBounceAnimator.setStartDelay(getResources().getInteger(R.integer.typing_indicator__pause_random_time));
        chatheadBounceAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (typingUsers.size() > 0 &&
                    chatheadBounceAnimator != null) {
                    chatheadBounceAnimator.start();
                } else {
                    chathead.animate()
                            .alpha(0f)
                            .translationY(0f)
                            .setDuration(getResources().getInteger(R.integer.framework_animation_duration_long))
                            .setInterpolator(new Quart.EaseOut())
                            .start();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        chatheadBounceAnimator.start();
    }

    private void stopAnimation() {
        typingUsers.clear();

        if (chatheadBounceAnimator != null) {
            chatheadBounceAnimator.setRepeatCount(0);
            chatheadBounceAnimator.cancel();
            chatheadBounceAnimator = null;
        }

        chathead.animate()
                .alpha(0f)
                .translationY(0f)
                .scaleX(CHATHEAD_ANIMATION_INITIAL_SCALE)
                .scaleY(CHATHEAD_ANIMATION_INITIAL_SCALE)
                .setDuration(getResources().getInteger(R.integer.framework_animation_duration_short))
                .setInterpolator(new Quart.EaseOut())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (chathead == null) {
                            return;
                        }
                        chathead.setAlpha(0f);
                        chathead.setTranslationY(0f);
                        chathead.setScaleX(CHATHEAD_ANIMATION_INITIAL_SCALE);
                        chathead.setScaleY(CHATHEAD_ANIMATION_INITIAL_SCALE);
                    }
                })
                .start();
    }

    public void hide() {
        stopAnimation();
    }

    @Override
    public void showSelfTyping(boolean show) {
        selfChatheadView.setVisibility(show ? VISIBLE : GONE);
        chathead.setVisibility(show ? GONE : VISIBLE);
    }
}
