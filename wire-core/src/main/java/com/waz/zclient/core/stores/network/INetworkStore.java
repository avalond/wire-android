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
package com.waz.zclient.core.stores.network;

import android.support.annotation.Nullable;
import com.waz.zclient.core.stores.IStore;

public interface INetworkStore extends IStore {

    void tearDown();

    /* has any */
    boolean hasInternetConnection();

    boolean hasWifiConnection();

    /**
     * A null NetworkAction implies that no work will be done if there is a connection, but the user will be notified
     * if there is no connection.
     * @param networkAction
     *
     * TODO this null hack (or the old `notifyNoInternet` method are only really needed because SE doesn't tell us when something failed
     * TODO due to no connection - perhaps we can have that passed down through the ConnectionIndicator?
     */
    void doIfHasInternetOrNotifyUser(@Nullable NetworkAction networkAction);

    void addNetworkStoreObserver(NetworkStoreObserver networkStoreObserver);

    void removeNetworkStoreObserver(NetworkStoreObserver networkStoreObserver);
}
