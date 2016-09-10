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
package com.waz.zclient.pages.main.conversation.views.row.message.views;

import android.content.Context;
import android.support.annotation.StringRes;
import com.waz.api.Message;
import com.waz.zclient.R;
import com.waz.zclient.core.controllers.tracking.events.media.PlayedSpotifyMessageEvent;
import com.waz.zclient.core.controllers.tracking.attributes.RangedAttribute;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.ui.theme.ThemeUtils;

public class SpotifyMessageViewController extends MediaPlayerViewController {

    public SpotifyMessageViewController(Context context, MessageViewsContainer messageViewsContainer) {
        super(context, messageViewsContainer);
    }

    @Override
    @StringRes
    public int getSource() {
        return R.string.mediaplayer__source__spotify;
    }

    @Override
    protected int getSourceImage() {
        // The logo view should be added as layout res that can be styled...dirty hack
        if (ThemeUtils.isDarkTheme(context)) {
            return R.drawable.ic_action_spotify_dark;
        }
        return R.drawable.ic_action_spotify;
    }

    @Override
    protected boolean isSeekingEnabled() {
        return messageViewsContainer.getControllerFactory().getSpotifyController().isLoggedIn();
    }

    @Override
    public void play() {
        super.play();
        messageViewsContainer.getConversationType();
        messageViewsContainer.getControllerFactory()
                             .getTrackingController()
                             .tagEvent(new PlayedSpotifyMessageEvent(!message.getUser().isMe(),
                                                                     messageViewsContainer.getStoreFactory().getConversationStore().getCurrentConversation()));
        messageViewsContainer.getControllerFactory()
                             .getTrackingController()
                             .updateSessionAggregates(RangedAttribute.SPOTIFY_CONTENT_CLICKS);
    }

    @Override
    public void onComplete(Message message) {
        super.onComplete(message);
        if (getPlayerController() == null ||
            !getPlayerController().isSelectedMessage(this.message)) {
            return;
        }

        if (!messageViewsContainer.getControllerFactory().getSpotifyController().shouldShowLoginHint()) {
            return;
        }

        mediaPlayerView.animateHint(R.string.mediaplayer__spotify__hint);
    }

    @Override
    protected int getDuration() {
        if (messageViewsContainer.getControllerFactory()
                                 .getSpotifyController()
                                 .isLoggedIn()) {
            return super.getDuration();
        } else {
            return 30 * 1000; // 30 sec
        }
    }

    @Override
    public boolean onHintClicked() {
        if (super.onHintClicked()) {
            return true;
        }
        mediaPlayerView.hideHint();
        messageViewsContainer.openSpotifySettings();
        return true;
    }
}
