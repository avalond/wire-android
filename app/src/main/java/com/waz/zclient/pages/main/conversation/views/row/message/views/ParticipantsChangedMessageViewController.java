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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.waz.api.IConversation;
import com.waz.api.Message;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.controllers.accentcolor.AccentColorObserver;
import com.waz.zclient.core.api.scala.ModelObserver;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.pages.main.participants.dialog.DialogLaunchMode;
import com.waz.zclient.pages.main.pickuser.controller.IPickUserController;
import com.waz.zclient.ui.text.GlyphTextView;
import com.waz.zclient.ui.utils.TextViewUtils;
import com.waz.zclient.ui.views.ZetaButton;
import com.waz.zclient.utils.LayoutSpec;
import com.waz.zclient.utils.ViewUtils;

import java.util.Locale;

public class ParticipantsChangedMessageViewController extends MessageViewController implements AccentColorObserver {

    private Locale locale;
    private View view;
    private AutoFitColumnRecyclerView gridView;
    private GlyphTextView iconView;
    private TextView messageTextView;
    private View inviteBanner;
    private ZetaButton inviteBannerShowContactButton;
    private ParticipantsChangedAdapter adapter;

    private final ModelObserver<IConversation> conversationModelObserver = new ModelObserver<IConversation>() {
        @Override
        public void updated(IConversation conversation) {
            if (inviteBannerShowContactButton == null) {
                return;
            }
            inviteBannerShowContactButton.setEnabled(conversation.isMemberOfConversation());
        }
    };
    private final ModelObserver<Message> messageModelObserver = new ModelObserver<Message>() {
        @Override
        public void updated(Message model) {
            final boolean membersAdded = message.getMessageType() == Message.Type.MEMBER_JOIN;
            if (membersAdded) {
                iconView.setText(R.string.glyph__plus);
            } else {
                iconView.setText(R.string.glyph__minus);
            }
            if (adapter == null) {
                adapter = new ParticipantsChangedAdapter(model.getMembers(), !membersAdded);
                adapter.setOnUserClickedListener(new OnUserClickedListener() {
                    @Override
                    public void onUserClicked(View view, User user) {
                        messageViewsContainer.getControllerFactory()
                                             .getConversationScreenController()
                                             .setPopoverLaunchedMode(DialogLaunchMode.COMMON_USER);
                        if (!messageViewsContainer.isPhone()) {
                            messageViewsContainer.getControllerFactory()
                                                 .getPickUserController()
                                                 .showUserProfile(message.getMembers()[0], view);
                        } else {
                            messageViewsContainer.getControllerFactory()
                                                 .getConversationScreenController()
                                                 .showUser(message.getMembers()[0]);
                        }
                    }
                });
                gridView.setAdapter(adapter);
            } else {
                adapter.setItems(model.getMembers());
            }
            userModelObserver.setAndUpdate(model.getMembers());
        }
    };
    private final ModelObserver<User> userModelObserver = new ModelObserver<User>() {
        @Override
        public void updated(User user) {
            final StringBuilder memberString = new StringBuilder();
            final User[] members = message.getMembers();
            for (int i = 0; i < members.length; i++) {
                final User member = members[i];
                memberString.append(getMemberSeparator(i, members.length));
                String name = member.isMe() ? context.getString(R.string.content__system__you)
                                            : member.getDisplayName();
                memberString.append(name);
            }

            final String message = getLinkText(memberString.toString());
            messageTextView.setText(message.toUpperCase(locale));
            TextViewUtils.boldText(messageTextView);
        }
    };

    @SuppressLint("InflateParams")
    public ParticipantsChangedMessageViewController(Context context, MessageViewsContainer messageViewContainer) {
        super(context, messageViewContainer);

        view = View.inflate(context, R.layout.row_conversation_participants_changed, null);
        messageTextView = ViewUtils.getView(view, R.id.ttv__row_conversation__people_changed__text);
        gridView = ViewUtils.getView(view, R.id.rv__row_conversation__people_changed__grid);
        gridView.setColumnSpacing(context.getResources().getDimensionPixelSize(R.dimen.wire__padding__small));
        iconView = ViewUtils.getView(view, R.id.gtv__row_conversation__people_changed__icon);
        inviteBannerShowContactButton = ViewUtils.getView(view, R.id.zb__conversation__invite_banner__show_contacts);
        inviteBanner = ViewUtils.getView(view, R.id.ll__conversation__invite_banner);
        locale = context.getResources().getConfiguration().locale;
        inviteBanner.setVisibility(View.GONE);

        final boolean isDarkTheme = this.messageViewsContainer.getControllerFactory().getThemeController().isDarkTheme();
        inviteBannerShowContactButton.setIsFilled(isDarkTheme);
        inviteBannerShowContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParticipantsChangedMessageViewController.this.messageViewsContainer.getControllerFactory().getPickUserController().showPickUser(IPickUserController.Destination.CURSOR, null);
            }
        });
    }

    @Override
    public void onSetMessage(Separator separator) {
        messageModelObserver.setAndUpdate(message);
        if (message.getMessageType() == Message.Type.MEMBER_JOIN &&
            LayoutSpec.isPhone(context) &&
            messageViewsContainer.getConversationType() == IConversation.Type.GROUP &&
            separator.previousMessage == null) {
            inviteBanner.setVisibility(View.VISIBLE);
            conversationModelObserver.setAndUpdate(message.getConversation());
        } else {
            inviteBanner.setVisibility(View.GONE);
        }

        messageViewsContainer.getControllerFactory().getAccentColorController().addAccentColorObserver(this);
        if (messageViewsContainer.getConversationType() == IConversation.Type.ONE_TO_ONE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void recycle() {
        if (!messageViewsContainer.isTornDown()) {
            messageViewsContainer.getControllerFactory().getAccentColorController().removeAccentColorObserver(this);
        }
        messageModelObserver.clear();
        userModelObserver.clear();
        conversationModelObserver.clear();

        if (adapter != null) {
            gridView.setAdapter(null);
            adapter.clear();
            adapter = null;
        }

        super.recycle();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Link Text
    //
    //////////////////////////////////////////////////////////////////////////////////////////

    private String getMemberSeparator(int index, int length) {
        String separator = "";
        boolean isLast = index == (length - 1);
        if (isLast && length > 1) {
            separator = " " + context.getString(R.string.content__system__last_item_separator) + " ";
        } else if (index > 0) {
            separator = context.getString(R.string.content__system__item_separator) + " ";
        }
        return separator;
    }

    private String getLinkText(String memberString) {
        // Flags
        final boolean isMe = message.getUser().isMe();
        final boolean started = message.getMessageType() == Message.Type.MEMBER_JOIN && message.isCreateConversation();
        final boolean added = message.getMessageType() == Message.Type.MEMBER_JOIN;
        final boolean removedSelf = didUserRemoveHimself(message);

        if (isMe) {
            if (started) {
                return context.getString(R.string.content__system__you_started_participant, "", memberString);
            }
            if (added) {
                return context.getString(R.string.content__system__you_added_participant, "", memberString);
            }
            if (removedSelf) {
                return context.getString(R.string.content__system__you_left);
            }
            return context.getString(R.string.content__system__you_removed_other, "", memberString);
        }

        final boolean addedOrRemovedMe = didUserAddOrRemoveMe(message);
        if (added) {
            if (addedOrRemovedMe) {
                if (started) {
                    return context.getString(R.string.content__system__other_started_you, message.getUser().getDisplayName());
                }
                return context.getString(R.string.content__system__other_added_you, message.getUser().getDisplayName());
            }
            if (started) {
                return context.getString(R.string.content__system__other_started_participant, message.getUser().getDisplayName(), memberString);
            }
            return context.getString(R.string.content__system__other_added_participant, message.getUser().getDisplayName(), memberString);
        }

        if (removedSelf) {
            return context.getString(R.string.content__system__other_left,
                                     message.getUser().getDisplayName());
        }

        if (addedOrRemovedMe) {
            return context.getString(R.string.content__system__other_removed_you,
                                     message.getUser().getDisplayName());
        }

        return context.getString(R.string.content__system__other_removed_other,
                                 message.getUser().getDisplayName(),
                                 memberString);
    }

    /**
     * Check if user removed himself
     */
    private boolean didUserRemoveHimself(Message message) {
        //check if user removed self
        if (message.getMembers().length == 1) {
            User user = message.getMembers()[0];
            return message.getUser().getId().equals(user.getId());
        }

        return false;
    }

    private boolean didUserAddOrRemoveMe(Message message) {
        if (message.getMembers().length == 1) {
            return message.getMembers()[0].isMe();
        }
        return false;
    }

    @Override
    public View getView() {
        return view;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //  AccentColorObserver
    //
    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onAccentColorHasChanged(Object sender, int color) {
        inviteBannerShowContactButton.setAccentColor(color);
    }

    public interface OnUserClickedListener {
        void onUserClicked(View view, User user);
    }
}
