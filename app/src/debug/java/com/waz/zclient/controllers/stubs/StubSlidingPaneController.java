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

import android.view.View;
import com.waz.zclient.pages.main.conversationpager.controller.ISlidingPaneController;
import com.waz.zclient.pages.main.conversationpager.controller.SlidingPaneObserver;
import java.lang.Override;

public class StubSlidingPaneController implements ISlidingPaneController {
  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void addObserver(SlidingPaneObserver slideListener) {
    ;
  }

  @Override
  public void onPanelClosed(View panel) {
    ;
  }

  @Override
  public void onPanelSlide(View panel, float slideOffset) {
    ;
  }

  @Override
  public void onPanelOpened(View panel) {
    ;
  }

  @Override
  public void removeObserver(SlidingPaneObserver slideListener) {
    ;
  }
}
