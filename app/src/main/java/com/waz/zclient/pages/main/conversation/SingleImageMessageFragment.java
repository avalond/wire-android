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
package com.waz.zclient.pages.main.conversation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.waz.api.ImageAsset;
import com.waz.api.Message;

public class SingleImageMessageFragment extends SingleImageFragment {

    public static final String TAG = SingleImageMessageFragment.class.getName();
    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    private Message message;

    public static SingleImageMessageFragment newInstance(Message message) {
        SingleImageMessageFragment fragment = new SingleImageMessageFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_MESSAGE, message);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        message = (Message) getArguments().get(ARG_MESSAGE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        message = null;
        super.onDestroyView();
    }

    @Override
    protected ImageAsset getImage() {
        return message.getImage();
    }

    @Override
    protected ImageView.ScaleType getScaleType() {
        return ImageView.ScaleType.FIT_CENTER;
    }
}
