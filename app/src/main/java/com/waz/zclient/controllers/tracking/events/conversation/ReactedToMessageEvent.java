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
package com.waz.zclient.controllers.tracking.events.conversation;


import android.support.annotation.NonNull;
import com.waz.api.IConversation;
import com.waz.api.Message;
import com.waz.zclient.core.controllers.tracking.attributes.Attribute;
import com.waz.zclient.core.controllers.tracking.events.Event;

public class ReactedToMessageEvent extends Event {

    public enum Method {
        BUTTON("button"),
        MENU("menu"),
        DOUBLE_TAP("douple_tap")
        ;

        public final String name;
        Method(String name) {
            this.name = name;
        }
    }

    public enum Action {
        LIKE("like"),
        UNLIKE("unlike")
        ;

        public final String name;
        Action(String name) {
            this.name = name;
        }
    }

    public static ReactedToMessageEvent like(IConversation conversation, Message message, Method method) {
        return new ReactedToMessageEvent(Action.LIKE, conversation, message, method);
    }

    public static ReactedToMessageEvent unlike(IConversation conversation, Message message, Method method) {
        return new ReactedToMessageEvent(Action.UNLIKE, conversation, message, method);
    }

    public ReactedToMessageEvent(Action action, IConversation conversation, Message message, Method method) {
        attributes.put(Attribute.ACTION, action.name);
        attributes.put(Attribute.TYPE, message.getMessageType().name());
        attributes.put(Attribute.METHOD, method.name);
        attributes.put(Attribute.USER, !message.getUser().isMe() ? "receiver" : "sender");
        attributes.put(Attribute.IS_LAST_MESSAGE, String.valueOf(message.isLastMessageFromOther() || message.isLastMessageFromSelf()));
        attributes.put(Attribute.CONVERSATION_TYPE, conversation.getType().name());
        attributes.put(Attribute.WITH_BOT, String.valueOf(conversation.isOtto()));
    }

    @NonNull
    @Override
    public String getName() {
        return "conversation.reacted_to_message";
    }
}
