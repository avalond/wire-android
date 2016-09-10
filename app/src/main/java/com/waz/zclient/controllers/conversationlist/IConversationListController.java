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
package com.waz.zclient.controllers.conversationlist;

import android.support.annotation.IntDef;

public interface IConversationListController {

    @IntDef({SCROLLED_TO_BOTTOM,
             NOT_SCROLLED_TO_BOTTOM,
             UNDEFINED
    })
    @interface ListScrollPosition { }
    int SCROLLED_TO_BOTTOM = 0;
    int NOT_SCROLLED_TO_BOTTOM = 1;
    int UNDEFINED = 2;

    void tearDown();

    void addConversationListObserver(ConversationListObserver conversationListObserver);

    void removeConversationListObserver(ConversationListObserver conversationListObserver);

    void notifyScrollOffsetChanged(int offset, @ListScrollPosition int scrolledToBottom);

    void onListViewOffsetChanged(int offset);

    void onReleasedPullDownFromBottom(int offset);
}
