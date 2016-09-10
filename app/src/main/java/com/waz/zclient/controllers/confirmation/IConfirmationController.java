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
package com.waz.zclient.controllers.confirmation;

import android.support.annotation.IntDef;

public interface IConfirmationController {

    @IntDef({BLOCK_PENDING,
             BLOCK_CONNECTED,
             LEAVE_CONVERSATION,
             REMOVE_USER_FROM_CONVERSATION,
             ADD_USER_TO_CONVERSATION,
             DELETE_CONVERSATION,
             SEND_MESSAGES_TO_DEGRADED_CONVERSATION
    })
    @interface ConfirmationMenuRequestType { }

    int BLOCK_PENDING = 0;
    int BLOCK_CONNECTED = 1;
    int LEAVE_CONVERSATION = 2;
    int REMOVE_USER_FROM_CONVERSATION = 3;
    int ADD_USER_TO_CONVERSATION = 4;
    int DELETE_CONVERSATION = 5;
    int SEND_MESSAGES_TO_DEGRADED_CONVERSATION = 6;

    @IntDef({CONVERSATION_LIST,
             PARTICIPANTS,
             USER_PROFILE,
             CONVERSATION
    })
    @interface ConfirmationMenuRequester { }

    int CONVERSATION_LIST = 0;
    int PARTICIPANTS = 1;
    int USER_PROFILE = 2;
    int CONVERSATION = 3;

    void tearDown();

    void addConfirmationObserver(ConfirmationObserver confirmationObserver);

    void removeConfirmationObserver(ConfirmationObserver confirmationObserver);

    void requestConfirmation(ConfirmationRequest confirmationRequest, @ConfirmationMenuRequester int requester);
}
