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
package com.waz.zclient.controllers.background;

import com.waz.api.ImageAsset;
import com.waz.api.Self;

public interface IBackgroundController {
    void setSelf(Self self);

    void setImageAsset(ImageAsset imageAsset);

    void addBackgroundObserver(BackgroundObserver backgroundObserver);

    void removeBackgroundObserver(BackgroundObserver backgroundObserver);

    void onStop();

    void update();

    void tearDown();

    void expand(boolean expand);
}
