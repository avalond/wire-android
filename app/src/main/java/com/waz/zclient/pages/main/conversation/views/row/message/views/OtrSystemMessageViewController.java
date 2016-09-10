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

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.waz.api.UpdateListener;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.controllers.accentcolor.AccentColorObserver;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.ui.text.TypefaceTextView;
import com.waz.zclient.ui.utils.TextViewUtils;
import com.waz.zclient.utils.OtrDestination;
import com.waz.zclient.utils.ViewUtils;

import java.util.Locale;

public class OtrSystemMessageViewController extends MessageViewController implements UpdateListener,
                                                                                     AccentColorObserver {

    private View view;
    private TypefaceTextView messageTextView;
    private ImageView shieldView;
    private int accentColor;
    private Locale locale;

    private Runnable onClickedNewDeviceMessageRunnable;
    private User[] users;
    private User sender;

    public OtrSystemMessageViewController(final Context context, final MessageViewsContainer messageViewContainer) {
        super(context, messageViewContainer);
        view = View.inflate(context, R.layout.row_conversation_otr_system_message, null);
        messageTextView = ViewUtils.getView(view, R.id.ttv__otr_added_new_device__message);
        shieldView = ViewUtils.getView(view, R.id.sv__otr__system_message);

        locale = context.getResources().getConfiguration().locale;

        onClickedNewDeviceMessageRunnable = new Runnable() {
            @Override
            public void run() {
                switch (message.getMessageType()) {
                    case OTR_UNVERIFIED:
                        if (OtrSystemMessageViewController.this.users[0].isMe()) {
                            OtrSystemMessageViewController.this.messageViewsContainer.openDevicesPage(OtrDestination.PREFERENCES, shieldView);
                        } else {
                            OtrSystemMessageViewController.this.messageViewsContainer.openDevicesPage(OtrDestination.PARTICIPANTS, shieldView);
                        }
                        break;
                    case OTR_DEVICE_ADDED:
                        if (OtrSystemMessageViewController.this.users[0].isMe()) {
                            OtrSystemMessageViewController.this.messageViewsContainer.openDevicesPage(OtrDestination.PREFERENCES, shieldView);
                        } else {
                            OtrSystemMessageViewController.this.messageViewsContainer.openDevicesPage(OtrDestination.PARTICIPANTS, shieldView);
                        }
                        break;
                    case STARTED_USING_DEVICE:
                        OtrSystemMessageViewController.this.messageViewsContainer.openDevicesPage(OtrDestination.PREFERENCES, shieldView);
                        break;
                    case OTR_ERROR:
                        OtrSystemMessageViewController.this.messageViewsContainer.onOpenUrl(context.getString(R.string.url_otr_decryption_error_1));
                        break;
                    case OTR_IDENTITY_CHANGED:
                        OtrSystemMessageViewController.this.messageViewsContainer.onOpenUrl(context.getString(R.string.url_otr_decryption_error_2));
                        break;

                }
            }
        };
    }

    @Override
    public void onAccentColorHasChanged(Object sender, int color) {
        accentColor = color;
        updated();
    }

    @Override
    protected void onSetMessage(Separator separator) {
        messageTextView.setText("");
        connectUsers(message.getMembers(), message.getUser());
        messageViewsContainer.getControllerFactory().getAccentColorController().addAccentColorObserver(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void recycle() {
        if (!messageViewsContainer.isTornDown()) {
            messageViewsContainer.getControllerFactory().getAccentColorController().removeAccentColorObserver(this);
        }
        disconnectUsers();
        super.recycle();
    }

    @Override
    public void updated() {
        if (sender == null || users == null) {
            messageTextView.setText("");
            return;
        }
        switch (message.getMessageType()) {
            case OTR_ERROR:
                setupOtrError();
                break;
            case OTR_IDENTITY_CHANGED:
                setupIdentityChangedError();
                break;
            case STARTED_USING_DEVICE:
                setupStartedUsingThisDevice();
                break;
            case HISTORY_LOST:
                setupLostHistory();
                break;
            case OTR_VERIFIED:
                setupDeviceVerified();
                break;
            case OTR_UNVERIFIED:
                setupDeviceUnverified();
                break;
            case OTR_DEVICE_ADDED:
                setupAddNewDevice();
                break;
        }
    }

    private void setupOtrError() {
        shieldView.setImageResource(R.drawable.red_alert);
        String label = context.getString(R.string.content__otr__message_error,
                                         sender.getDisplayName().toUpperCase(locale));
        setLabel(label);
    }

    private void setupIdentityChangedError() {
        shieldView.setImageResource(R.drawable.red_alert);
        String label = context.getString(R.string.content__otr__identity_changed_error,
                                         sender.getDisplayName().toUpperCase(locale));
        setLabel(label);
    }

    private void setupLostHistory() {
        shieldView.setImageResource(R.drawable.red_alert);
        String label = context.getString(R.string.content__otr__lost_history).toUpperCase(locale);
        setLabel(label);
    }

    private void setupStartedUsingThisDevice() {
        shieldView.setImageBitmap(null);
        messageTextView.setTypeface(context.getString(R.string.wire__typeface__light));
        String label = context.getString(R.string.content__otr__start_this_device__message).toUpperCase(locale);
        setLabel(label);
    }

    private void setupDeviceVerified() {
        shieldView.setImageResource(R.drawable.shield_full);
        String label = context.getString(R.string.content__otr__all_fingerprints_verified).toUpperCase(locale);
        setLabel(label);
    }

    private void setupDeviceUnverified() {
        shieldView.setImageResource(R.drawable.shield_half);
        String formattedLabel;

        if (users.length == 1 && users[0].isMe()) {
            formattedLabel = context.getString(R.string.content__otr__your_unverified_device__message);
        } else {
            formattedLabel = context.getString(R.string.content__otr__unverified_device__message, getOthersDisplayName().toUpperCase(locale));
        }

        setLabel(formattedLabel);
    }

    private void setupAddNewDevice() {
        shieldView.setImageResource(R.drawable.shield_half);
        String formattedLabel;

        if (users.length == 1 && users[0].isMe()) {
            formattedLabel = context.getString(R.string.content__system__you);
        } else {
            formattedLabel = getOthersDisplayName();
        }

        String label = context.getString(R.string.content__otr__added_new_device__message, formattedLabel.toUpperCase(locale));
        setLabel(label);
    }

    private void setLabel(String label) {
        messageTextView.setText(label);
        TextViewUtils.boldText(messageTextView);
        TextViewUtils.linkifyText(messageTextView, accentColor, false, false, onClickedNewDeviceMessageRunnable);
    }

    private String getOthersDisplayName() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < users.length; i++) {
            sb.append(users[i].getDisplayName());

            if (i != users.length - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    private void connectUsers(User[] users, User sender) {
        this.users = users;
        for (User user : users) {
            user.addUpdateListener(this);
        }
        this.sender = sender;
        this.sender.addUpdateListener(this);

        updated();
    }

    private void disconnectUsers() {
        if (users != null) {
            for (User user : users) {
                user.removeUpdateListener(this);
            }
            users = null;
        }
        if (sender != null) {
            sender.removeUpdateListener(this);
            sender = null;
        }
    }
}
