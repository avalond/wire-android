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
import android.net.Uri;
import com.waz.api.IConversation;
import com.waz.zclient.controllers.sharing.ISharingController;
import com.waz.zclient.controllers.sharing.SharedContentType;
import com.waz.zclient.controllers.sharing.SharingObserver;
import java.lang.Override;
import java.lang.String;
import java.util.List;

public class StubSharingController implements ISharingController {
  @Override
  public void setSharedUris(List<Uri> imageUris) {
    ;
  }

  @Override
  public List<Uri> getSharedFileUris() {
    return null;
  }

  @Override
  public void onContentShared(Activity activity, IConversation toConversation, List<Uri> sharedUris) {
    ;
  }

  @Override
  public void maybeResetSharedText(IConversation currentConversation) {
    ;
  }

  @Override
  public void setDestination(IConversation conversation) {
    ;
  }

  @Override
  public void onContentShared(Activity activity, IConversation toConversation) {
    ;
  }

  @Override
  public void onContentShared(Activity activity, IConversation toConversation, String sharedText) {
    ;
  }

  @Override
  public void maybeResetSharedUris(IConversation currentConversation) {
    ;
  }

  @Override
  public void addObserver(SharingObserver observer) {
    ;
  }

  @Override
  public String getSharingConversation() {
    return null;
  }

  @Override
  public SharedContentType getSharedContentType() {
    return null;
  }

  @Override
  public void setSharingConversationId(String conversationId) {
    ;
  }

  @Override
  public String getSharedText() {
    return null;
  }

  @Override
  public void setSharedContentType(SharedContentType type) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public boolean isSharedConversation(IConversation conversation) {
    return false;
  }

  @Override
  public void setSharedText(String text) {
    ;
  }

  @Override
  public IConversation getDestination() {
    return null;
  }

  @Override
  public void removeObserver(SharingObserver observer) {
    ;
  }
}
