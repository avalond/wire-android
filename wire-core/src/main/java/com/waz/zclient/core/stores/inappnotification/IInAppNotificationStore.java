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
package com.waz.zclient.core.stores.inappnotification;

import android.support.annotation.Nullable;
import com.waz.api.ErrorsList;
import com.waz.api.IConversation;
import com.waz.api.Message;
import com.waz.zclient.core.stores.IStore;

public interface IInAppNotificationStore extends IStore {

    /* adds an observer for incoming messages */
    void addInAppNotificationObserver(InAppNotificationStoreObserver messageListener);

    /* removes an observer of incoming messages */
    void removeInAppNotificationObserver(InAppNotificationStoreObserver messageListener);

    void setUserLookingAtPeoplePicker(boolean userLookingAtPeoplePicker);

    void setUserLookingAtParticipants(boolean userLookingAtParticipants);

    void setUserSendingPicture(boolean userSendingPicture);

    void setIsLandscape(boolean isInLandscape);

    void dismissError(String errorId);

    @Nullable ErrorsList.ErrorDescription getError(String errorId);

    ErrorsList getErrorList();

    boolean shouldShowChatheads(IConversation currentConversation, Message message);

    void onScrolledToBottom();

    void onScrolledAwayFromBottom();

}
