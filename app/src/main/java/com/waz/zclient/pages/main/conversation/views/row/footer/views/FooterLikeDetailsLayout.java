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
package com.waz.zclient.pages.main.conversation.views.row.footer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.core.api.scala.ModelObserver;
import com.waz.zclient.utils.StringUtils;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.views.chathead.ChatheadImageView;

public class FooterLikeDetailsLayout extends LinearLayout {

    private static final int NUM_LIKE_USERS_TO_SHOW_AVATARS = 3;

    private User firstUser;
    private User secondUser;

    private TextView description;
    private TextView hintArrow;
    private View chatheadContainer;
    private ChatheadImageView firstChathead;
    private ChatheadImageView secondChathead;
    private OnClickListener onClickListener;

    private boolean showHint;

    private final ModelObserver<User> descriptionModelObserver = new ModelObserver<User>() {
        @Override
        public void updated(User user) {
            StringBuilder stringBuilder = new StringBuilder(firstUser.getDisplayName());
            if (secondUser != null) {
                stringBuilder.append(", ").append(secondUser.getDisplayName());
            }
            description.setText(stringBuilder.toString());
            description.invalidate();
        }
    };

    public FooterLikeDetailsLayout(Context context) {
        this(context, null);
    }

    public FooterLikeDetailsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterLikeDetailsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.row_conversation_footer_like_details, this, true);
        setOrientation(HORIZONTAL);
        description = ViewUtils.getView(this, R.id.tv__footer__like__description);
        chatheadContainer = ViewUtils.getView(this, R.id.ll__like_chathead_container);
        firstChathead = ViewUtils.getView(this, R.id.cv__first_like_chathead);
        secondChathead = ViewUtils.getView(this, R.id.cv__second_like_chathead);
        hintArrow = ViewUtils.getView(this, R.id.gtv__footer__like__arrow);
        if (StringUtils.isRTL()) {
            // Arrow needs to point in other direction for RTL locales
            hintArrow.setText(context.getString(R.string.glyph__next));
        }
        chatheadContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClickedLikersAvatars();
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setUsers(User[] users, boolean showHint) {
        this.showHint = showHint;
        if (users == null || users.length == 0) {
            clearUsers();
            return;
        }

        firstUser = users[users.length - 1];
        secondUser = users.length > 1 ? users[users.length - 2] : null;

        if (users.length >= NUM_LIKE_USERS_TO_SHOW_AVATARS) {
            hintArrow.setVisibility(INVISIBLE);
            description.setText(getResources().getQuantityString(R.plurals.message_footer__number_of_likes, users.length, users.length));
            description.invalidate();
            chatheadContainer.setVisibility(VISIBLE);
            firstChathead.setUser(firstUser);
            secondChathead.setUser(secondUser);
        } else {
            hintArrow.setVisibility(INVISIBLE);
            chatheadContainer.setVisibility(GONE);
            descriptionModelObserver.clear();
            descriptionModelObserver.setAndUpdate(firstUser);
            if (secondUser != null) {
                descriptionModelObserver.addAndUpdate(secondUser);
            }
            firstChathead.setUser(null);
            secondChathead.setUser(null);
        }
    }

    private void clearUsers() {
        if (showHint) {
            hintArrow.setVisibility(INVISIBLE);
            description.setText(R.string.message_footer__tap_to_like);
        } else {
            hintArrow.setVisibility(GONE);
            description.setText("");
        }
        descriptionModelObserver.clear();
        firstUser = null;
        secondUser = null;
        chatheadContainer.setVisibility(GONE);
        firstChathead.setUser(null);
        secondChathead.setUser(null);
    }

    public interface OnClickListener {
        void onClickedLikersAvatars();
    }

}
