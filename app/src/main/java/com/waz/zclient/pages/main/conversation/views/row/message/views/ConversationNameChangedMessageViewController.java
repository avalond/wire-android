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
import android.view.View;
import android.widget.TextView;
import com.waz.api.UpdateListener;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.ui.utils.TextViewUtils;
import com.waz.zclient.utils.ViewUtils;

import java.util.Locale;

public class ConversationNameChangedMessageViewController extends MessageViewController implements UpdateListener {

    private View view;
    private TextView changedByUser;
    private TextView newConversationName;
    private User user;
    private Locale locale;

    @SuppressLint("InflateParams")
    public ConversationNameChangedMessageViewController(final Context context, MessageViewsContainer messageViewContainer) {
        super(context, messageViewContainer);
        view = View.inflate(context, R.layout.row_conversation_name_changed, null);
        newConversationName = ViewUtils.getView(view, R.id.ttv__row_conversation__new_conversation_name);
        changedByUser = ViewUtils.getView(view, R.id.ttv__row_conversation__conversation_name_changed_by_user);
        locale = context.getResources().getConfiguration().locale;
    }

    @Override
    public void onSetMessage(Separator separator) {
        message.addUpdateListener(this);
        user = message.getUser();
        user.addUpdateListener(this);
        updated();
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
        super.recycle();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void updated() {
        if (message.getNewConversationName() == null) {
            changedByUser.setText("");
            newConversationName.setText("");
            newConversationName.setVisibility(View.GONE);
            return;
        }

        final String name;
        if (user == null) {
            name = "";
        } else if (user.isMe()) {
            name = context.getResources().getString(R.string.content__system__you_renamed_conv);
        } else {
            name = context.getResources().getString(R.string.content__system__other_renamed_conv,
                                                    message.getUser().getDisplayName().toUpperCase(locale));
        }

        changedByUser.setText(name);
        newConversationName.setText(message.getNewConversationName());

        TextViewUtils.boldText(changedByUser);
    }
}
