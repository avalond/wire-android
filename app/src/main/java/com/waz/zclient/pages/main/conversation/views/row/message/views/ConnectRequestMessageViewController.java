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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.waz.api.IConversation;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.core.api.scala.ModelObserver;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.ui.utils.TextViewUtils;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.common.views.ChatheadView;

public class ConnectRequestMessageViewController extends MessageViewController {

    // User with whom conversation was created
    private View view;
    private ChatheadView chatheadView;
    private TextView usernameTextView;
    private TextView label;
    private final ModelObserver<User> userModelObserver = new ModelObserver<User>() {
        @Override
        public void updated(User user) {
            if (context == null ||
                label == null ||
                messageViewsContainer == null) {
                return;
            }
            usernameTextView.setText(user.getName());
            if (user.isAutoConnection()) {
                label.setText(R.string.content__message__connect_request__auto_connect__footer);
                TextViewUtils.linkifyText(label, label.getCurrentTextColor(), true, true, new Runnable() {
                    @Override
                    public void run() {
                        messageViewsContainer.onOpenUrl(context.getString(R.string.url__help));
                    }
                });
            } else {
                label.setText(R.string.content__message__connect_request__footer);
            }
        }
    };


    @SuppressLint("InflateParams")
    public ConnectRequestMessageViewController(Context context, MessageViewsContainer messageViewsContainer) {
        super(context, messageViewsContainer);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.row_conversation_connect_request, null);
        chatheadView = ViewUtils.getView(view, R.id.cv__row_conversation__connect_request__chat_head);
        usernameTextView = ViewUtils.getView(view, R.id.ttv__row_conversation__connect_request__user);
        label = ViewUtils.getView(view, R.id.ttv__row_conversation__connect_request__label);
    }

    @Override
    public void recycle() {
        userModelObserver.pauseListening();
        super.recycle();
    }

    @Override
    protected void onSetMessage(Separator separator) {
        // TODO this crashes when the conversation is a group conversation
        if (message.getConversation().getType() == IConversation.Type.ONE_TO_ONE) {
            chatheadView.setUser(message.getConversation().getOtherParticipant());
        }

        // TODO this crashes when the conversation is a group conversation
        if (message.getConversation().getType() == IConversation.Type.ONE_TO_ONE) {
            userModelObserver.setAndUpdate(message.getConversation().getOtherParticipant());
        }
    }

    @Override
    public View getView() {
        return view;
    }
}
