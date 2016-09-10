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
package com.waz.zclient.core.stores.stub;

import com.waz.api.Contacts;
import com.waz.api.User;
import com.waz.zclient.core.stores.pickuser.IPickUserStore;
import com.waz.zclient.core.stores.pickuser.PickUserStoreObserver;
import java.lang.Override;
import java.lang.String;

public class StubPickUserStore implements IPickUserStore {
  @Override
  public void loadSearchByFilter(String filter, int numberOfResults, boolean excludeUsers) {
    ;
  }

  @Override
  public String[] getExcludedUsers() {
    return null;
  }

  @Override
  public void addPickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver) {
    ;
  }

  @Override
  public Contacts getContacts() {
    return null;
  }

  @Override
  public void removePickUserStoreObserver(PickUserStoreObserver pickUserStoreObserver) {
    ;
  }

  @Override
  public void setExcludedUsers(String[] users) {
    ;
  }

  @Override
  public void loadContacts() {
    ;
  }

  @Override
  public void searchContacts(String query) {
    ;
  }

  @Override
  public boolean hasTopUsers() {
    return false;
  }

  @Override
  public void resetContactSearch() {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void loadTopUserList(int numberOfResults, boolean excludeUsers) {
    ;
  }

  @Override
  public User getUser(String userId) {
    return null;
  }
}
