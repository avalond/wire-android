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

import com.waz.api.IConversation;
import com.waz.api.User;
import com.waz.zclient.controllers.mentioning.IMentioningController;
import com.waz.zclient.controllers.mentioning.MentioningObserver;
import java.lang.Override;
import java.lang.String;

public class StubMentioningController implements IMentioningController {
  @Override
  public void removeObserver(MentioningObserver observer) {
    ;
  }

  @Override
  public void completeUser(User user) {
    ;
  }

  @Override
  public String extractQuery(int cursorPosition, String text) {
    return null;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void query(String query, float x, float y) {
    ;
  }

  @Override
  public void setCurrentConversation(IConversation conversation) {
    ;
  }

  @Override
  public void addObserver(MentioningObserver observer) {
    ;
  }

  @Override
  public void hide() {
    ;
  }
}
