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
package com.waz.zclient.controllers.streammediaplayer;

import android.support.annotation.NonNull;
import com.waz.api.MediaAsset;
import com.waz.api.MediaProvider;
import com.waz.api.Message;
import com.waz.zclient.controllers.mediaplayer.MediaPlayerState;

import java.util.List;

public interface IStreamMediaPlayerController {

    void addStreamMediaObserver(StreamMediaPlayerObserver streamMediaObserver);

    void removeStreamMediaObserver(StreamMediaPlayerObserver streamMediaObserver);

    void addStreamMediaBarObserver(StreamMediaBarObserver streamMediaBarObserver);

    void removeStreamMediaBarObserver(StreamMediaBarObserver streamMediaBarObserver);

    void tearDown();

    void setMediaPlayerInstance(MediaProvider type);

    void play();

    void play(String conversationId);

    void play(Message message, MediaAsset mediaTrack);

    void pause();

    void pause(String conversationId);

    int getPosition(Message message);

    void seekTo(Message message, int positionMs);

    void release(Message message);

    MediaPlayerState getMediaPlayerState(Message message);

    MediaPlayerState getMediaPlayerState(String conversationId);

    boolean isSelectedConversation(String conversationId);

    @NonNull
    Message getMessage();

    void stop();

    void stop(String conversationId);

    void informVisibleItems(List<String> visibleMessageIds);

    void requestScroll();

    boolean isSelectedMessage(Message message);

    @NonNull
    MediaAsset getMediaTrack();

    void resetMediaPlayer(MediaProvider type);
}
