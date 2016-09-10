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
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.waz.zclient.pages.main.conversation.views.row.message.ConversationItemViewController;

public class RecyclingLinearLayout extends LinearLayout {

    private ConversationItemViewController controller;

    public RecyclingLinearLayout(Context context) {
        this(context, null);
    }

    public RecyclingLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setViewController(ConversationItemViewController controller) {
        this.controller = controller;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (controller != null) {
            controller.recycle();
        }
    }

}
