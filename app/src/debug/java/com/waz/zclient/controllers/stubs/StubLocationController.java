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
package com.waz.zclient.controllers.stubs;

import com.waz.api.MessageContent;
import com.waz.zclient.controllers.location.ILocationController;
import com.waz.zclient.controllers.location.LocationObserver;
import java.lang.Override;

public class StubLocationController implements ILocationController {
  @Override
  public void removeObserver(LocationObserver observer) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void hideShareLocation(MessageContent.Location location) {
    ;
  }

  @Override
  public void addObserver(LocationObserver observer) {
    ;
  }

  @Override
  public void showShareLocation() {
    ;
  }
}
