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

import com.waz.zclient.controllers.password.IPasswordController;
import java.lang.Override;
import java.lang.String;

public class StubPasswordController implements IPasswordController {
  @Override
  public void setPassword(String password) {
    ;
  }

  @Override
  public boolean hasPassword() {
    return false;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void reset() {
    ;
  }

  @Override
  public String getPassword() {
    return null;
  }
}
