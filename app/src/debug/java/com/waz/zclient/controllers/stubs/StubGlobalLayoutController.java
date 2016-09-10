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
import android.view.View;
import com.waz.zclient.controllers.globallayout.GlobalLayoutObserver;
import com.waz.zclient.controllers.globallayout.IGlobalLayoutController;
import com.waz.zclient.controllers.globallayout.KeyboardHeightObserver;
import com.waz.zclient.controllers.globallayout.KeyboardVisibilityObserver;
import com.waz.zclient.controllers.globallayout.StatusBarVisibilityObserver;
import com.waz.zclient.controllers.navigation.Page;
import java.lang.Override;

public class StubGlobalLayoutController implements IGlobalLayoutController {
  @Override
  public void removeStatusBarVisibilityObserver(StatusBarVisibilityObserver observer) {
    ;
  }

  @Override
  public void removeGlobalLayoutObserver(GlobalLayoutObserver globalLayoutObserver) {
    ;
  }

  @Override
  public void addKeyboardVisibilityObserver(KeyboardVisibilityObserver keyboardVisibilityObserver) {
    ;
  }

  @Override
  public void addStatusBarVisibilityObserver(StatusBarVisibilityObserver observer) {
    ;
  }

  @Override
  public void removeKeyboardVisibilityObserver(KeyboardVisibilityObserver keyboardVisibilityObserver) {
    ;
  }

  @Override
  public void addKeyboardHeightObserver(KeyboardHeightObserver keyboardHeightObserver) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public int getSoftInputModeForPage(Page page) {
    return 0;
  }

  @Override
  public void removeKeyboardHeightObserver(KeyboardHeightObserver keyboardHeightObserver) {
    ;
  }

  @Override
  public void setSoftInputModeForPage(Page page) {
    ;
  }

  @Override
  public boolean isKeyboardVisible() {
    return false;
  }

  @Override
  public void setActivity(Activity activity) {
    ;
  }

  @Override
  public void showStatusBar(Activity activity) {
    ;
  }

  @Override
  public void setGlobalLayout(View view) {
    ;
  }

  @Override
  public void addGlobalLayoutObserver(GlobalLayoutObserver globalLayoutObserver) {
    ;
  }

  @Override
  public void hideStatusBar(Activity activity) {
    ;
  }
}
