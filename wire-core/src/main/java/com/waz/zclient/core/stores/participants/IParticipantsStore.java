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
package com.waz.zclient.core.stores.participants;

import com.waz.api.IConversation;
import com.waz.api.UsersList;
import com.waz.zclient.core.stores.IStore;

public interface IParticipantsStore extends IStore {

    // adds an observer on this store
    void addParticipantsStoreObserver(ParticipantsStoreObserver participantsStoreObserver);

    // removes an observer on this store
    void removeParticipantsStoreObserver(ParticipantsStoreObserver participantsStoreObserver);

    UsersList getParticipants();

    void setCurrentConversation(IConversation conversation);
}
