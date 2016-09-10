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
package com.waz.zclient.pages.main.conversation.views.row.separator;

import com.waz.api.Message;
import com.waz.zclient.BuildConfig;
import com.waz.zclient.utils.DateConvertUtils;
import org.threeten.bp.ZonedDateTime;
import timber.log.Timber;

public class SeparatorRules {

    private static final String TAG = SeparatorRules.class.getName();

    public static boolean shouldHaveName(Message message, Separator separator) {
        if (message.getMessageType() == Message.Type.UNKNOWN && !BuildConfig.SHOW_DEVELOPER_OPTIONS) {
            return false;
        }
        if (message.isEdited()) {
            return true;
        }

        // First message with no previous messages e.g. when history has been cleared
        if (separator.getPreviousMessage() == null &&
                separator.getNextMessage().getMessageType() != Message.Type.MEMBER_JOIN &&
                separator.getNextMessage().getMessageType() != Message.Type.MEMBER_LEAVE &&
                separator.getNextMessage().getMessageType() != Message.Type.CONNECT_REQUEST &&
                separator.getNextMessage().getMessageType() != Message.Type.KNOCK &&
                separator.getNextMessage().getMessageType() != Message.Type.RENAME &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_ERROR &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_VERIFIED &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_UNVERIFIED &&
                separator.getNextMessage().getMessageType() != Message.Type.STARTED_USING_DEVICE &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_DEVICE_ADDED &&
                separator.getNextMessage().getMessageType() != Message.Type.HISTORY_LOST &&
                separator.getNextMessage().getMessageType() != Message.Type.MISSED_CALL) {
            return true;
        }

        return separator.getPreviousMessage() != null &&

               // messages are from different users, or the previous message is not text or image or rich media
               (!separator.getPreviousMessage().getUser().getId().equals(separator.getNextMessage().getUser().getId()) ||
                (separator.getPreviousMessage().getUser().getId().equals(separator.getNextMessage().getUser().getId()) &&
                 separator.getPreviousMessage().getMessageType() != Message.Type.ASSET &&
                 separator.getPreviousMessage().getMessageType() != Message.Type.LOCATION &&
                 separator.getPreviousMessage().getMessageType() != Message.Type.TEXT &&
                 separator.getPreviousMessage().getMessageType() != Message.Type.RICH_MEDIA
                ) ||
                separator.getNextMessage().getMessageType() == Message.Type.ANY_ASSET ||
                separator.getNextMessage().getMessageType() == Message.Type.RECALLED
               ) &&

               //next message is not a "system" message or a knock
                separator.getNextMessage().getMessageType() != Message.Type.MEMBER_JOIN &&
                separator.getNextMessage().getMessageType() != Message.Type.MEMBER_LEAVE &&
                separator.getNextMessage().getMessageType() != Message.Type.CONNECT_REQUEST &&
                separator.getNextMessage().getMessageType() != Message.Type.KNOCK &&
                separator.getNextMessage().getMessageType() != Message.Type.RENAME &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_ERROR &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_VERIFIED &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_UNVERIFIED &&
                separator.getNextMessage().getMessageType() != Message.Type.STARTED_USING_DEVICE &&
                separator.getNextMessage().getMessageType() != Message.Type.OTR_DEVICE_ADDED &&
                separator.getNextMessage().getMessageType() != Message.Type.HISTORY_LOST &&
                separator.getNextMessage().getMessageType() != Message.Type.MISSED_CALL;
    }

    public static boolean shouldHaveTimestamp(Separator separator, int timeBetweenMessagesToTriggerTimestamp) {
        if (separator.getPreviousMessage() == null || separator.getNextMessage() == null) {
            return false;
        }

        if (separator.getNextMessage().getMessageType() == Message.Type.MISSED_CALL) {
            return false;
        }

        try {
            ZonedDateTime previousMessageTime = DateConvertUtils.asZonedDateTime(separator.getPreviousMessage().getTime());
            ZonedDateTime nextMessageTIme = DateConvertUtils.asZonedDateTime(separator.getNextMessage().getTime());
            return previousMessageTime.isBefore(nextMessageTIme.minusSeconds(timeBetweenMessagesToTriggerTimestamp));
        } catch (Exception e) {
            Timber.e(e, "Failed Separator timestamp check! Couldn't parse received time: either '%s' or '%s', or both.",
                separator.getPreviousMessage().getTime(),
                separator.getNextMessage().getTime());
            return false;
        }
    }

    public static boolean shouldHaveUnreadDot(Separator separator, int unreadMessageCount) {
        return unreadMessageCount > 0 &&
               separator.getLastReadMessage() != null &&
               separator.getPreviousMessage() != null &&
               !separator.getPreviousMessage().getId().equals(separator.getNextMessage().getId()) &&
               separator.getLastReadMessage().getId().equals(separator.getPreviousMessage().getId());
    }

    public static boolean shouldHaveBigTimestamp(Separator separator) {
        if (separator.getPreviousMessage() == null || separator.getNextMessage() == null) {
            return false;
        }

        try {
            ZonedDateTime previousMessageTime = DateConvertUtils.asZonedDateTime(separator.getPreviousMessage().getTime());
            ZonedDateTime nextMessageTime = DateConvertUtils.asZonedDateTime(separator.getNextMessage().getTime());
            return previousMessageTime.toLocalDate().atStartOfDay().isBefore(nextMessageTime.toLocalDate().atStartOfDay());
        } catch (Exception e) {
            Timber.e(e, "Failed Separator timestamp check! Couldn't parse received time: either '%s' or '%s', or both.",
                     separator.getPreviousMessage().getTime(),
                     separator.getNextMessage().getTime());
            return false;
        }
    }
}
