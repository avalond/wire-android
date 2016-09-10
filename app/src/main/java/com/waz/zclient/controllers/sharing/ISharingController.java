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
package com.waz.zclient.controllers.sharing;

import android.app.Activity;
import android.net.Uri;
import com.waz.api.IConversation;

import java.util.List;

public interface ISharingController {

    void addObserver(SharingObserver observer);

    void removeObserver(SharingObserver observer);

    void tearDown();

    void setSharedContentType(SharedContentType type);

    SharedContentType getSharedContentType();

    void setSharedText(String text);

    String getSharedText();

    void setSharedUris(List<Uri> imageUris);

    List<Uri> getSharedFileUris();

    void onContentShared(Activity activity, IConversation toConversation);

    void onContentShared(Activity activity, IConversation toConversation, String sharedText);

    void onContentShared(Activity activity, IConversation toConversation, List<Uri> sharedUris);

    void setSharingConversationId(String conversationId);

    String getSharingConversation();

    void maybeResetSharedText(IConversation currentConversation);

    void maybeResetSharedUris(IConversation currentConversation);

    boolean isSharedConversation(IConversation conversation);

    void setDestination(IConversation conversation);

    IConversation getDestination();
}
