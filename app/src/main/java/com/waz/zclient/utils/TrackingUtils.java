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
package com.waz.zclient.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import com.waz.api.AudioAssetForUpload;
import com.waz.api.AudioEffect;
import com.waz.api.ContactMethod;
import com.waz.api.IConversation;
import com.waz.api.Message;
import com.waz.api.Permission;
import com.waz.zclient.R;
import com.waz.zclient.controllers.drawing.DrawingController;
import com.waz.zclient.controllers.tracking.ITrackingController;
import com.waz.zclient.controllers.tracking.events.connect.SentConnectRequestEvent;
import com.waz.zclient.controllers.tracking.events.connect.SentInviteToContactEvent;
import com.waz.zclient.controllers.tracking.events.optionsmenu.OptionsMenuItemSelectedEvent;
import com.waz.zclient.core.controllers.tracking.attributes.CompletedMediaType;
import com.waz.zclient.core.controllers.tracking.attributes.ConversationType;
import com.waz.zclient.core.controllers.tracking.attributes.RangedAttribute;
import com.waz.zclient.core.controllers.tracking.events.media.CompletedMediaActionEvent;
import com.waz.zclient.core.controllers.tracking.events.media.OpenedMediaActionEvent;
import com.waz.zclient.core.controllers.tracking.events.media.SentAudioMessageEvent;
import com.waz.zclient.core.controllers.tracking.events.media.SentLocationEvent;
import com.waz.zclient.core.controllers.tracking.events.media.SentPictureEvent;
import com.waz.zclient.core.controllers.tracking.events.media.SentTextMessageEvent;
import com.waz.zclient.core.controllers.tracking.events.settings.ChangedContactsPermissionEvent;
import com.waz.zclient.core.controllers.tracking.events.settings.ChangedSoundNotificationLevelEvent;
import com.waz.zclient.core.stores.connect.IConnectStore;
import com.waz.zclient.pages.extendedcursor.image.ImagePreviewLayout;
import com.waz.zclient.ui.optionsmenu.OptionsMenuItem;

import java.util.Locale;

public class TrackingUtils {

    public static void tagOptionsMenuSelectedEvent(ITrackingController trackingController, OptionsMenuItem optionsMenuItem, IConversation.Type conversationType, boolean inConversationList, boolean openedBySwipe) {

        OptionsMenuItemSelectedEvent.Action action = getEventAction(optionsMenuItem);
        if (action == null) {
            return;
        }

        ConversationType type = conversationType == IConversation.Type.GROUP ? ConversationType.GROUP_CONVERSATION
                                                                             : ConversationType.ONE_TO_ONE_CONVERSATION;

        OptionsMenuItemSelectedEvent.Context context = inConversationList ?
                                                       OptionsMenuItemSelectedEvent.Context.LIST :
                                                       OptionsMenuItemSelectedEvent.Context.PARTICIPANTS;

        OptionsMenuItemSelectedEvent.Method method = openedBySwipe ?
                                                     OptionsMenuItemSelectedEvent.Method.SWIPE :
                                                     OptionsMenuItemSelectedEvent.Method.TAP;

        trackingController.tagEvent(new OptionsMenuItemSelectedEvent(action,
                                                                     context,
                                                                     type,
                                                                     method));
    }

    public static OptionsMenuItemSelectedEvent.Action getEventAction(OptionsMenuItem optionsMenuItem) {
        OptionsMenuItemSelectedEvent.Action action = null;
        switch (optionsMenuItem) {
            case ARCHIVE:
                action = OptionsMenuItemSelectedEvent.Action.ARCHIVE;
                break;
            case UNARCHIVE:
                action = OptionsMenuItemSelectedEvent.Action.UNARCHIVE;
                break;
            case SILENCE:
                action = OptionsMenuItemSelectedEvent.Action.SILENCE;
                break;
            case UNSILENCE:
                action = OptionsMenuItemSelectedEvent.Action.NOTIFY;
                break;
            case BLOCK:
                action = OptionsMenuItemSelectedEvent.Action.BLOCK;
                break;
            case UNBLOCK:
                action = OptionsMenuItemSelectedEvent.Action.UNBLOCK;
                break;
            case DELETE:
                action = OptionsMenuItemSelectedEvent.Action.DELETE;
                break;
            case LEAVE:
                action = OptionsMenuItemSelectedEvent.Action.LEAVE;
                break;
            case RENAME:
                action = OptionsMenuItemSelectedEvent.Action.RENAME;
                break;
        }
        return action;
    }

    public static void tagSentInviteToContactEvent(ITrackingController trackingController,
                                                   ContactMethod.Kind contactMethodKind,
                                                   boolean isResending,
                                                   boolean fromContactSearch) {
        SentInviteToContactEvent event = new SentInviteToContactEvent(contactMethodKind == ContactMethod.Kind.EMAIL ?
                                                                      SentInviteToContactEvent.Method.EMAIL :
                                                                      SentInviteToContactEvent.Method.PHONE,
                                                                      isResending,
                                                                      fromContactSearch);
        trackingController.tagEvent(event);
    }

    public static void tagSentConnectRequestFromUserProfileEvent(ITrackingController trackingController,
                                                                 IConnectStore.UserRequester userRequester,
                                                                 int numSharedUsers) {
        SentConnectRequestEvent.EventContext eventContext;
        switch (userRequester) {
            case SEARCH:
                eventContext = SentConnectRequestEvent.EventContext.STARTUI;
                break;
            case PARTICIPANTS:
                eventContext = SentConnectRequestEvent.EventContext.PARTICIPANTS;
                break;
            default:
                eventContext = SentConnectRequestEvent.EventContext.UNKNOWN;
                break;
        }

        SentConnectRequestEvent event = new SentConnectRequestEvent(eventContext, numSharedUsers);
        trackingController.tagEvent(event);
    }

    public static String messageTypeForMessageSelection(Message.Type messageType) {
        String type;
        switch (messageType) {
            case TEXT:
                type = "text";
                break;
            case ASSET:
                type = "image";
                break;
            case ANY_ASSET:
                type = "file";
                break;
            case RICH_MEDIA:
                type = "rich_media";
                break;
            case VIDEO_ASSET:
                type = "video";
                break;
            case AUDIO_ASSET:
                type = "audio";
                break;
            case KNOCK:
                type = "ping";
                break;
            default:
                type = messageType.name().toLowerCase(Locale.getDefault());
                break;
        }
        return type;
    }

    public static String getMessageSelectionMode(boolean multipleMessagesSelected) {
        return multipleMessagesSelected ?
               "multiple" :
               "single";
    }

    public static void tagChangedContactsPermissionEvent(ITrackingController trackingController,
                                                         String[] permissions,
                                                         int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (!permission.contains(Permission.READ_CONTACTS.toString())) {
                continue;
            }
            boolean grantedContactsPermission = grantResults[i] == PackageManager.PERMISSION_GRANTED ? true : false;
            trackingController.tagEvent(new ChangedContactsPermissionEvent(grantedContactsPermission, false));
        }
    }

    public static void tagChangedSoundNotificationLevelEvent(ITrackingController trackingController,
                                                             String preferenceString,
                                                             Context context) {
        ChangedSoundNotificationLevelEvent.Level level = ChangedSoundNotificationLevelEvent.Level.ALL_SOUNDS;
        if (preferenceString.equals(context.getString(R.string.pref_sound_value_none))) {
            level = ChangedSoundNotificationLevelEvent.Level.NO_SOUNDS;
        } else if (preferenceString.equals(context.getString(R.string.pref_sound_value_some))) {
            level = ChangedSoundNotificationLevelEvent.Level.SOME_SOUNDS;
        }

        trackingController.tagEvent(new ChangedSoundNotificationLevelEvent(level));
    }

    public static void tagSentAudioMessageEvent(ITrackingController trackingController,
                                                AudioAssetForUpload audioAssetForUpload,
                                                AudioEffect appliedAudioEffect,
                                                boolean fromMinimisedState,
                                                boolean sentWithQuickAction,
                                                IConversation conversation) {
        int durationSec = (int) audioAssetForUpload.getDuration().getSeconds();


        SentAudioMessageEvent.AudioEffectType audioEffectType = SentAudioMessageEvent.AudioEffectType.NONE;
        if (appliedAudioEffect != null) {
            switch (appliedAudioEffect) {
                case PITCH_UP_INSANE:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.HELIUM;
                    break;
                case PITCH_DOWN_INSANE:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.JELLY_FISH;
                    break;
                case PACE_UP_MED:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.HARE;
                    break;
                case REVERB_MAX:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.CATHEDRAL;
                    break;
                case CHORUS_MAX:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.ALIEN;
                    break;
                case VOCODER_MED:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.ROBOT;
                    break;
                case REVERSE:
                    audioEffectType = SentAudioMessageEvent.AudioEffectType.UPSIDE_DOWN;
                    break;
            }
        }

        trackingController.tagEvent(new SentAudioMessageEvent(durationSec,
                                                              audioEffectType,
                                                              sentWithQuickAction,
                                                              fromMinimisedState,
                                                              conversation));
    }

    public static void onSentTextMessage(ITrackingController trackingController, IConversation conversation) {
        trackingController.tagEvent(new SentTextMessageEvent(conversation));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.TEXT,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));
    }

    public static void onSentGifMessage(ITrackingController trackingController, IConversation conversation) {
        trackingController.tagEvent(new SentTextMessageEvent(conversation));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.TEXT,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));

        trackingController.tagEvent(new SentPictureEvent(SentPictureEvent.Source.GIPHY,
                                                         conversation.getType().name(),
                                                         SentPictureEvent.Method.DEFAULT,
                                                         SentPictureEvent.SketchSource.NONE,
                                                         conversation.isOtto()));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.PHOTO,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));
    }

    public static void onSentSketchMessage(ITrackingController trackingController,
                                           IConversation conversation,
                                           DrawingController.DrawingDestination drawingDestination) {

        SentPictureEvent.SketchSource sketchSource = SentPictureEvent.SketchSource.NONE;
        switch (drawingDestination) {
            case CAMERA_PREVIEW_VIEW:
                sketchSource = SentPictureEvent.SketchSource.CAMERA_GALLERY;
                break;
            case SKETCH_BUTTON:
                sketchSource = SentPictureEvent.SketchSource.SKETCH_BUTTON;
                break;
            case SINGLE_IMAGE_VIEW:
                sketchSource = SentPictureEvent.SketchSource.IMAGE_FULL_VIEW;
                break;
        }

        trackingController.tagEvent(new SentPictureEvent(SentPictureEvent.Source.SKETCH,
                                                         conversation.getType().name(),
                                                         SentPictureEvent.Method.DEFAULT,
                                                         sketchSource,
                                                         conversation.isOtto()));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.PHOTO,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));
        trackingController.updateSessionAggregates(RangedAttribute.IMAGES_SENT);
    }

    public static void onSentLocationMessage(ITrackingController trackingController, IConversation conversation) {
        trackingController.tagEvent(new SentLocationEvent(conversation));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.LOCATION,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));
    }

    public static void onSentPhotoMessageFromSharing(ITrackingController trackingController,
                                                     IConversation conversation) {

        trackingController.tagEvent(new SentPictureEvent(SentPictureEvent.Source.SHARING,
                                                         conversation.getType().name(),
                                                         SentPictureEvent.Method.DEFAULT,
                                                         SentPictureEvent.SketchSource.NONE,
                                                         conversation.isOtto()));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.PHOTO,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));
        trackingController.updateSessionAggregates(RangedAttribute.IMAGES_SENT);
    }

    public static void onSentPhotoMessage(ITrackingController trackingController,
                                          IConversation conversation,
                                          SentPictureEvent.Source source,
                                          SentPictureEvent.Method method) {
        trackingController.tagEvent(new SentPictureEvent(source,
                                                         conversation.getType().name(),
                                                         method,
                                                         SentPictureEvent.SketchSource.NONE,
                                                         conversation.isOtto()));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.PHOTO,
                                                                  conversation.getType().name(),
                                                                  conversation.isOtto()));
        trackingController.updateSessionAggregates(RangedAttribute.IMAGES_SENT);
    }


    public static void onSentPhotoMessage(ITrackingController trackingController,
                                          IConversation conversation,
                                          ImagePreviewLayout.Source source) {
        SentPictureEvent.Source eventSource = source == ImagePreviewLayout.Source.CAMERA ?
                                              SentPictureEvent.Source.CAMERA :
                                              SentPictureEvent.Source.GALLERY;
        SentPictureEvent.Method eventMethod = SentPictureEvent.Method.DEFAULT;
        switch (source) {
            case CAMERA:
            case IN_APP_GALLERY:
                eventMethod = SentPictureEvent.Method.KEYBOARD;
                break;
            case DEVICE_GALLERY:
                eventMethod = SentPictureEvent.Method.FULL_SCREEN;
                break;
        }
        onSentPhotoMessage(trackingController,
                           conversation,
                           eventSource,
                           eventMethod);
    }


    public static void onSentPingMessage(ITrackingController trackingController, IConversation conversation) {
        boolean isGroupConversation = conversation.getType() == IConversation.Type.GROUP;
        trackingController.tagEvent(OpenedMediaActionEvent.ping(isGroupConversation));
        trackingController.tagEvent(new CompletedMediaActionEvent(CompletedMediaType.PING,
                                                                  isGroupConversation ? "GROUP" : "ONE_TO_ONE",
                                                                  conversation.isOtto()));
    }
}
