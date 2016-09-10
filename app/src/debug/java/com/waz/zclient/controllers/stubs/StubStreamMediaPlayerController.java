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

import com.waz.api.MediaAsset;
import com.waz.api.MediaProvider;
import com.waz.api.Message;
import com.waz.zclient.controllers.mediaplayer.MediaPlayerState;
import com.waz.zclient.controllers.streammediaplayer.IStreamMediaPlayerController;
import com.waz.zclient.controllers.streammediaplayer.StreamMediaBarObserver;
import com.waz.zclient.controllers.streammediaplayer.StreamMediaPlayerObserver;
import java.lang.Override;
import java.lang.String;
import java.util.List;

public class StubStreamMediaPlayerController implements IStreamMediaPlayerController {
  @Override
  public void removeStreamMediaBarObserver(StreamMediaBarObserver streamMediaBarObserver) {
    ;
  }

  @Override
  public Message getMessage() {
    return null;
  }

  @Override
  public boolean isSelectedConversation(String conversationId) {
    return false;
  }

  @Override
  public int getPosition(Message message) {
    return 0;
  }

  @Override
  public void play(Message message, MediaAsset mediaTrack) {
    ;
  }

  @Override
  public MediaPlayerState getMediaPlayerState(String conversationId) {
    return null;
  }

  @Override
  public void removeStreamMediaObserver(StreamMediaPlayerObserver streamMediaObserver) {
    ;
  }

  @Override
  public MediaPlayerState getMediaPlayerState(Message message) {
    return null;
  }

  @Override
  public void play() {
    ;
  }

  @Override
  public void addStreamMediaObserver(StreamMediaPlayerObserver streamMediaObserver) {
    ;
  }

  @Override
  public void addStreamMediaBarObserver(StreamMediaBarObserver streamMediaBarObserver) {
    ;
  }

  @Override
  public void setMediaPlayerInstance(MediaProvider type) {
    ;
  }

  @Override
  public void stop(String conversationId) {
    ;
  }

  @Override
  public void seekTo(Message message, int positionMs) {
    ;
  }

  @Override
  public void stop() {
    ;
  }

  @Override
  public void informVisibleItems(List<String> visibleMessageIds) {
    ;
  }

  @Override
  public void play(String conversationId) {
    ;
  }

  @Override
  public MediaAsset getMediaTrack() {
    return null;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void pause(String conversationId) {
    ;
  }

  @Override
  public boolean isSelectedMessage(Message message) {
    return false;
  }

  @Override
  public void pause() {
    ;
  }

  @Override
  public void requestScroll() {
    ;
  }

  @Override
  public void release(Message message) {
    ;
  }

  @Override
  public void resetMediaPlayer(MediaProvider type) {
    ;
  }
}
