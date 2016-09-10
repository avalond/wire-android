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

import android.app.Activity;
import com.waz.zclient.controllers.orientation.IOrientationController;
import com.waz.zclient.controllers.orientation.OrientationControllerObserver;
import com.waz.zclient.utils.SquareOrientation;
import java.lang.Override;

public class StubOrientationController implements IOrientationController {
  @Override
  public void removeOrientationControllerObserver(OrientationControllerObserver orientationControllerObserver) {
    ;
  }

  @Override
  public int getDeviceOrientation() {
    return 0;
  }

  @Override
  public SquareOrientation getLastKnownOrientation() {
    return null;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void setActivity(Activity activity) {
    ;
  }

  @Override
  public void addOrientationControllerObserver(OrientationControllerObserver orientationControllerObserver) {
    ;
  }

  @Override
  public int getActivityRotationDegrees() {
    return 0;
  }

  @Override
  public boolean isInPortrait() {
    return false;
  }
}
