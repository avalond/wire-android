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
package com.waz.zclient.pages.main.participants.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.waz.api.User;
import com.waz.zclient.R;
import com.waz.zclient.core.api.scala.ModelObserver;
import com.waz.zclient.pages.main.pickuser.views.UserRowView;
import com.waz.zclient.ui.text.TextTransform;
import com.waz.zclient.ui.text.TypefaceTextView;
import com.waz.zclient.ui.theme.ThemeUtils;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.common.views.ChatheadView;

public class ChatheadWithTextFooter extends LinearLayout implements UserRowView {

    protected ChatheadView chathead;
    protected TypefaceTextView footer;
    private TextTransform transformer;
    private User user = null;

    private ModelObserver<User> userModelObserver = new ModelObserver<User>() {
        @Override
        public void updated(User model) {
            user = model;
            chathead.setUser(user);
            footer.setText(transformer.transform(user.getDisplayName()));
        }
    };

    public ChatheadWithTextFooter(Context context) {
        this(context, null);
    }

    public ChatheadWithTextFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatheadWithTextFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.chathead_with_text_footer, this, true);
        setOrientation(VERTICAL);
        chathead = ViewUtils.getView(this, R.id.cv__chathead);
        footer = ViewUtils.getView(this, R.id.ttv__text_view);
        transformer = TextTransform.get(context.getResources().getString(R.string.participants__chathead__name_label__text_transform));
        if (ThemeUtils.isDarkTheme(context)) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
        initAttributes(attrs);
    }

    @Override
    public boolean isSelected() {
        return chathead.isSelected();
    }

    @Override
    public void setSelected(boolean selected) {
        chathead.setSelected(selected);
    }

    public void setUser(User user) {
        userModelObserver.setAndUpdate(user);
    }

    public User getUser() {
        return user;
    }

    @Override
    public void onClicked() {
        chathead.setSelected(!chathead.isSelected());
    }

    public void setChatheadFooterTextColor(int color) {
        footer.setTextColor(color);
    }

    public void setChatheadFooterFont(String fontName) {
        footer.setTypeface(fontName);
    }

    public void applyLightTheme() {
        footer.setTextColor(getResources().getColor(R.color.text__primary_light));
    }

    public void applyDarkTheme() {
        footer.setTextColor(getResources().getColor(R.color.text__primary_dark));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        userModelObserver.resumeListening();
    }

    @Override
    protected void onDetachedFromWindow() {
        userModelObserver.pauseListening();
        super.onDetachedFromWindow();
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(chathead);
            }
        });
    }

    public void setChatheadDimension(int size) {
        ViewUtils.setWidth(chathead, size);
        ViewUtils.setHeight(chathead, size);
    }

    public void setFooterWidth(int width) {
        ViewUtils.setWidth(footer, width);
    }

    private void initAttributes(AttributeSet attrs) {
        int chatheadSize;
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.ChatheadWithTextFooter);
            chatheadSize  = a.getDimensionPixelSize(R.styleable.ChatheadWithTextFooter_chathead_size, 0);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        if (chatheadSize > 0) {
            setChatheadDimension(chatheadSize);
        }
    }
}
