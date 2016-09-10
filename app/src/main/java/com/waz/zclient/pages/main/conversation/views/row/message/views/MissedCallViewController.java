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
package com.waz.zclient.pages.main.conversation.views.row.message.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.waz.api.MessagesList;
import com.waz.api.UpdateListener;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.controllers.accentcolor.AccentColorObserver;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.pages.main.participants.dialog.DialogLaunchMode;
import com.waz.zclient.ui.animation.interpolators.penner.Back;
import com.waz.zclient.ui.utils.ResourceUtils;
import com.waz.zclient.ui.utils.TextViewUtils;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.views.calling.StaticCallingIndicator;
import com.waz.zclient.views.chathead.ChatheadImageView;

import java.util.Locale;

public class MissedCallViewController extends MessageViewController implements UpdateListener,
                                                                               AccentColorObserver,
                                                                               View.OnClickListener {

    private View view;
    private TextView missedCallByUserTextView;
    private StaticCallingIndicator staticCallingIndicator;
    private ChatheadImageView chatheadImageView;
    private User user;
    private Locale locale;
    private AnimatorSet animatorSet;

    @SuppressLint("InflateParams")
    public MissedCallViewController(Context context, MessageViewsContainer messageViewContainer) {
        super(context, messageViewContainer);
        this.view = View.inflate(context, R.layout.row_conversation_missed_call, null);
        this.missedCallByUserTextView = ViewUtils.getView(view, R.id.ttv__row_conversation__missed_call);
        this.locale = context.getResources().getConfiguration().locale;
        this.staticCallingIndicator = ViewUtils.getView(view, R.id.sci__conversation__missed_call__image);
        this.chatheadImageView = ViewUtils.getView(view, R.id.civ__row_conversation__missed_call_chathead);
    }

    @Override
    protected void onSetMessage(Separator separator) {
        user = message.getUser();
        user.addUpdateListener(this);
        message.addUpdateListener(this);
        chatheadImageView.setUser(user);
        chatheadImageView.setOnClickListener(this);
        messageViewsContainer.getControllerFactory().getAccentColorController().addAccentColorObserver(this);

        final MessagesList messagesList = message.getConversation().getMessages();
        if (messagesList.size() - 1 == messagesList.getMessageIndex(message)) {
            animateCallIndicator();
        }
        updated();
    }

    private void animateCallIndicator() {
        final long duration = context.getResources().getInteger(R.integer.animation_duration_long);
        final long animationCircleDelay = context.getResources().getInteger(R.integer.row_conversation__missed_call__animation__delay_factor);
        final float overshoot = ResourceUtils.getResourceFloat(context.getResources(), R.dimen.row_conversation__missed_call__animation__overshoot);

        ObjectAnimator collapseInnerRadiusAnimator = ObjectAnimator.ofFloat(staticCallingIndicator, StaticCallingIndicator.INNER_RADIUS, 1.0f, 0.5f);
        collapseInnerRadiusAnimator.setDuration(duration);
        collapseInnerRadiusAnimator.setStartDelay(2 * animationCircleDelay);
        collapseInnerRadiusAnimator.setInterpolator(new Back.EaseIn(overshoot));

        ObjectAnimator expandInnerRadiusAnimator = ObjectAnimator.ofFloat(staticCallingIndicator, StaticCallingIndicator.INNER_RADIUS, 0.5f, 1.0f);
        expandInnerRadiusAnimator.setDuration(duration);
        expandInnerRadiusAnimator.setInterpolator(new Back.EaseOut(overshoot));

        AnimatorSet innerRadiusSet = new AnimatorSet();
        innerRadiusSet.playSequentially(collapseInnerRadiusAnimator, expandInnerRadiusAnimator);

        ObjectAnimator collapseMiddleRadiusAnimator = ObjectAnimator.ofFloat(staticCallingIndicator, StaticCallingIndicator.MIDDLE_RADIUS, 1.0f, 0.5f);
        collapseMiddleRadiusAnimator.setDuration(duration);
        collapseMiddleRadiusAnimator.setStartDelay(animationCircleDelay);
        collapseMiddleRadiusAnimator.setInterpolator(new Back.EaseIn(overshoot));

        ObjectAnimator expandMiddleRadiusAnimator = ObjectAnimator.ofFloat(staticCallingIndicator, StaticCallingIndicator.MIDDLE_RADIUS, 0.5f, 1.0f);
        expandMiddleRadiusAnimator.setDuration(duration);
        expandMiddleRadiusAnimator.setStartDelay(2 * animationCircleDelay);
        expandMiddleRadiusAnimator.setInterpolator(new Back.EaseOut(overshoot));

        AnimatorSet middleRadiusSet = new AnimatorSet();
        middleRadiusSet.playSequentially(collapseMiddleRadiusAnimator, expandMiddleRadiusAnimator);

        ObjectAnimator collapseOuterRadiusAnimator = ObjectAnimator.ofFloat(staticCallingIndicator, StaticCallingIndicator.OUTER_RADIUS, 1.0f, 0.5f);
        collapseOuterRadiusAnimator.setDuration(duration);
        collapseOuterRadiusAnimator.setInterpolator(new Back.EaseIn(overshoot));

        ObjectAnimator expandOuterRadiusAnimator = ObjectAnimator.ofFloat(staticCallingIndicator, StaticCallingIndicator.OUTER_RADIUS, 0.5f, 1.0f);
        expandOuterRadiusAnimator.setStartDelay(4 * animationCircleDelay);
        expandOuterRadiusAnimator.setDuration(duration);
        expandOuterRadiusAnimator.setInterpolator(new Back.EaseOut(overshoot));

        AnimatorSet outerRadiusSet = new AnimatorSet();
        outerRadiusSet.playSequentially(collapseOuterRadiusAnimator, expandOuterRadiusAnimator);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(innerRadiusSet, middleRadiusSet, outerRadiusSet);
        animatorSet.setStartDelay(context.getResources().getInteger(R.integer.animation_delay_very_long));
        animatorSet.start();
    }

    private void cancelAnimation() {
        if (animatorSet == null) {
            return;
        }
        animatorSet.cancel();
        animatorSet = null;
    }

    @Override
    public void recycle() {
        if (user != null) {
            user.removeUpdateListener(this);
            user = null;
        }
        if (message != null) {
            message.removeUpdateListener(this);
        }
        if (!messageViewsContainer.isTornDown()) {
            messageViewsContainer.getControllerFactory().getAccentColorController().removeAccentColorObserver(this);
        }
        cancelAnimation();
        chatheadImageView.setUser(null);
        chatheadImageView.setOnClickListener(null);
        super.recycle();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void updated() {
        if (message == null ||
            missedCallByUserTextView == null ||
            staticCallingIndicator == null ||
            messageViewsContainer == null ||
            messageViewsContainer.isTornDown()) {
            return;
        }

        if (user == null) {
            missedCallByUserTextView.setText("");
            return;
        }
        if (user.isMe()) {
            missedCallByUserTextView.setText(R.string.content__missed_call__you_called);
        } else {
            String username = message.getUser().getDisplayName();
            if (TextUtils.isEmpty(username)) {
                missedCallByUserTextView.setText("");
            } else {
                missedCallByUserTextView.setText(context.getString(R.string.content__missed_call__xxx_called,
                                                                   username.toUpperCase(locale)));
            }
        }

        TextViewUtils.boldText(missedCallByUserTextView);
    }

    @Override
    public void onAccentColorHasChanged(Object sender, int color) {
        staticCallingIndicator.setColor(color);
    }

    @Override
    public void onClick(View v) {
        if (messageViewsContainer.isTornDown()) {
            return;
        }
        messageViewsContainer.getControllerFactory()
                             .getConversationScreenController()
                             .setPopoverLaunchedMode(DialogLaunchMode.AVATAR);
        if (!messageViewsContainer.isPhone()) {
            messageViewsContainer.getControllerFactory()
                                 .getPickUserController()
                                 .showUserProfile(user, chatheadImageView);
        } else {
            messageViewsContainer.getControllerFactory()
                                 .getConversationScreenController()
                                 .showUser(user);
        }
    }
}
