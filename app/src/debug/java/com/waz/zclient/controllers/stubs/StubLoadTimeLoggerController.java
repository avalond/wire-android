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

import com.waz.zclient.controllers.loadtimelogger.ILoadTimeLoggerController;
import java.lang.Override;

public class StubLoadTimeLoggerController implements ILoadTimeLoggerController {
  @Override
  public void appStart() {
    ;
  }

  @Override
  public void loginFail() {
    ;
  }

  @Override
  public void loginPressed() {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void conversationContentSyncFinish() {
    ;
  }

  @Override
  public void clickConversationInList() {
    ;
  }

  @Override
  public void conversationContentSyncStart() {
    ;
  }

  @Override
  public void loginSuccess() {
    ;
  }

  @Override
  public void hideLaunchScreen() {
    ;
  }

  @Override
  public void conversationPageVisible() {
    ;
  }
}
