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
package com.waz.zclient.core.stores.pickuser;

import com.waz.api.Contacts;
import com.waz.api.IConversation;
import com.waz.api.User;

import java.util.HashSet;
import java.util.Set;

public abstract class PickUserStore implements IPickUserStore {
    // observers attached to a IPickUserStore
    protected Set<PickUserStoreObserver> pickUserStoreObservers = new HashSet<PickUserStoreObserver>();

    @Override
    public void addPickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver) {
        pickUserStoreObservers.add(pickUserStoreObserver);
    }

    @Override
    public void removePickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver) {
        pickUserStoreObservers.remove(pickUserStoreObserver);
    }

    protected void notifyTopUsersUpdated(User[] users) {
        for (PickUserStoreObserver pickUserStoreObserver : pickUserStoreObservers) {
            pickUserStoreObserver.onTopUsersUpdated(users);
        }
    }

    protected void notifySearchResultsUpdated(User[] contacts, User[] otherUsers, IConversation[] conversations) {
        for (PickUserStoreObserver pickUserStoreObserver : pickUserStoreObservers) {
            pickUserStoreObserver.onSearchResultsUpdated(contacts, otherUsers, conversations);
        }
    }

    protected void notifyContactsUpdated(Contacts contacts) {
        for (PickUserStoreObserver pickUserStoreObserver : pickUserStoreObservers) {
            pickUserStoreObserver.onContactsUpdated(contacts);
        }
    }

    protected void notifySearchContactsUpdated(Contacts contacts) {
        for (PickUserStoreObserver pickUserStoreObserver : pickUserStoreObservers) {
            pickUserStoreObserver.onSearchContactsUpdated(contacts);
        }
    }

}
