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

import com.waz.zclient.core.stores.network.INetworkStore;
import com.waz.zclient.core.stores.network.NetworkAction;
import com.waz.zclient.core.stores.network.NetworkStoreObserver;
import java.lang.Override;

public class StubNetworkStore implements INetworkStore {
  @Override
  public void addNetworkStoreObserver(NetworkStoreObserver networkStoreObserver) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public boolean hasInternetConnection() {
    return false;
  }

  @Override
  public boolean hasWifiConnection() {
    return false;
  }

  @Override
  public void doIfHasInternetOrNotifyUser(NetworkAction networkAction) {
    ;
  }

  @Override
  public void removeNetworkStoreObserver(NetworkStoreObserver networkStoreObserver) {
    ;
  }
}
