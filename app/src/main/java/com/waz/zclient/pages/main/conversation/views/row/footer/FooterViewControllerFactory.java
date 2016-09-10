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
package com.waz.zclient.pages.main.conversation.views.row.footer;

import android.content.Context;
import com.waz.api.Message;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;

public class FooterViewControllerFactory {

    public static FooterViewController create(Context context, Message message, MessageViewsContainer container) {
        if (showFooter(message)) {
            return new FooterViewController(context, container);
        }
        return null;
    }

    private static boolean showFooter(Message message) {
        switch (message.getMessageType()) {
            case TEXT:
            case TEXT_EMOJI_ONLY:
            case RICH_MEDIA:
            case LOCATION:
            case VIDEO_ASSET:
            case AUDIO_ASSET:
            case ANY_ASSET:
            case ASSET:
                return true;
            default:
                return false;
        }
    }
}
