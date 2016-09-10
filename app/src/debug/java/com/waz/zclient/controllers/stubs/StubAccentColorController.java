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

import com.waz.api.AccentColor;
import com.waz.zclient.controllers.accentcolor.AccentColorChangeRequester;
import com.waz.zclient.controllers.accentcolor.AccentColorObserver;
import com.waz.zclient.controllers.accentcolor.IAccentColorController;
import java.lang.Override;

public class StubAccentColorController implements IAccentColorController {
  @Override
  public int getColor() {
    return 0;
  }

  @Override
  public void removeAccentColorObserver(AccentColorObserver accentColorObserver) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void addAccentColorObserver(AccentColorObserver accentColorObserver) {
    ;
  }

  @Override
  public AccentColor getAccentColor() {
    return null;
  }

  @Override
  public void setColor(AccentColorChangeRequester accentColorChangeRequester, int color) {
    ;
  }
}
