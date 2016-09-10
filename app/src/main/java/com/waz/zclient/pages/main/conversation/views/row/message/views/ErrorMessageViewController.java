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
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.waz.zclient.BuildConfig;
import com.waz.zclient.R;
import com.waz.zclient.pages.main.conversation.views.MessageViewsContainer;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.utils.ViewUtils;

public class ErrorMessageViewController extends MessageViewController {

    private static final String ERROR_MESSAGETYPE_UNKNOWN = "Error - messagetype '%s' unknown";
    private TextView errorView;
    private FrameLayout view;

    @SuppressLint("InflateParams")
    public ErrorMessageViewController(Context context, MessageViewsContainer messageViewContainer) {
        super(context, messageViewContainer);
        errorView = new TextView(context);
        errorView.setTextColor(Color.GRAY);
        view = new FrameLayout(context);
        if (BuildConfig.SHOW_DEVELOPER_OPTIONS) {
            view.addView(errorView);
        }
        final Resources res = context.getResources();
        ViewUtils.setPaddingStart(errorView, res.getDimensionPixelSize(R.dimen.content__padding_left));
        ViewUtils.setPaddingEnd(errorView, res.getDimensionPixelSize(R.dimen.content__padding_right));
    }

    @Override
    public void onSetMessage(Separator separator) {
        errorView.setText(String.format(ERROR_MESSAGETYPE_UNKNOWN, message.getMessageType()));
    }

    @Override
    public View getView() {
        return view;
    }
}
