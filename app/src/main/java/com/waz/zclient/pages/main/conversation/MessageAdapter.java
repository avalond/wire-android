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

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.waz.api.Message;
import com.waz.api.MessagesList;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.footer.FooterViewController;
import com.waz.zclient.pages.main.conversation.views.row.footer.FooterViewControllerFactory;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageAndSeparatorViewController;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewControllerFactory;
import com.waz.zclient.pages.main.conversation.views.row.message.views.TextMessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.utils.MessageUtils;
import timber.log.Timber;


public class MessageAdapter extends BaseAdapter {
    private final MessageViewsContainer container;
    private int unreadMessageCount;
    private MessagesList messagesList;
    private Message lastReadMessage;

    public MessageAdapter(MessageViewsContainer container) {
        this.container = container;
    }

    public void resetState() {
        lastReadMessage = null;
        unreadMessageCount = 0;
        messagesList = null;
        notifyDataSetChanged();
    }

    public void setMessages(@NonNull MessagesList messagesList) {
        Timber.i("setMessages: %s", messagesList);
        this.messagesList = messagesList;
        int lastReadIndex = messagesList.getLastReadIndex();
        if (lastReadIndex < messagesList.size()) {
            if (lastReadIndex >= 0) {
                lastReadMessage = messagesList.get(lastReadIndex);
            } else {
                lastReadMessage = messagesList.getLastMessage();
            }
            unreadMessageCount = messagesList.getUnreadCount();
        }
        notifyDataSetChanged();
    }

    public int getIndexOfMessage(Message message) {
        if (messagesList == null || message == null) {
            return -1;
        }
        return messagesList.getMessageIndex(message);
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public int getLastReadIndex() {
        return getIndexOfMessage(lastReadMessage);
    }

    @Override
    public int getCount() {
        return messagesList != null ? messagesList.size() : 0;
    }

    @Override
    public int getViewTypeCount() {
        return MessageViewControllerFactory.getControllerTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        final Message message = getItem(position);
        final Message.Type messageType = message.getMessageType();
        if (messageType == Message.Type.RICH_MEDIA) {
            final Message.Part richMediaPart = MessageUtils.getFirstRichMediaPart(message);
            if (richMediaPart != null) {
                return MessageViewControllerFactory.getKeyPosition(richMediaPart.getPartType());
            } else {
                return MessageViewControllerFactory.getKeyPosition(Message.Type.TEXT);
            }
        } else {
            return MessageViewControllerFactory.getKeyPosition(messageType);
        }
    }

    @Override
    public Message getItem(int position) {
        return messagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageAndSeparatorViewController messageAndSeparator;

        Message message = getItem(position);
        Message prevMessage = position == 0 ? null : getItem(position - 1);
        Separator separator = new Separator(prevMessage, message, lastReadMessage);

        if (convertView == null) {
            messageAndSeparator = createNewMessageAndSeparatorViewController(parent, message);
            convertView = messageAndSeparator.getView();
            convertView.setTag(messageAndSeparator);
        } else {
            messageAndSeparator = (MessageAndSeparatorViewController) convertView.getTag();
            if (messageAndSeparator.getMessageViewController() instanceof TextMessageViewController &&
                message.getMessageType() == Message.Type.RICH_MEDIA) {
                // Link preview messages can change from TEXT type to RICH MEDIA
                messageAndSeparator = createNewMessageAndSeparatorViewController(parent, message);
                convertView = messageAndSeparator.getView();
                convertView.setTag(messageAndSeparator);
            }
        }

        messageAndSeparator.setModel(message, separator);

        return convertView;
    }

    private MessageAndSeparatorViewController createNewMessageAndSeparatorViewController(ViewGroup parent,
                                                                                         Message message) {
        MessageViewController messageViewController = MessageViewControllerFactory.create(parent.getContext(),
                                                                                          message,
                                                                                          container);
        FooterViewController footerViewController = FooterViewControllerFactory.create(parent.getContext(), message, container);
        return new MessageAndSeparatorViewController(messageViewController,
                                                     footerViewController,
                                                     container,
                                                     parent.getContext());
    }
}
