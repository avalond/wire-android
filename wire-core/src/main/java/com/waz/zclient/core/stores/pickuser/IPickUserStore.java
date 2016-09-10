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
import com.waz.api.User;
import com.waz.zclient.core.stores.IStore;

public interface IPickUserStore extends IStore {
    void addPickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver);

    void removePickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver);

    void loadTopUserList(int numberOfResults, boolean excludeUsers);

    void loadSearchByFilter(String filter, int numberOfResults, boolean excludeUsers);

    void loadContacts();

    void searchContacts(String query);

    void resetContactSearch();

    void setExcludedUsers(String[] users);

    String[] getExcludedUsers();

    boolean hasTopUsers();

    User getUser(String userId);

    Contacts getContacts();
}
