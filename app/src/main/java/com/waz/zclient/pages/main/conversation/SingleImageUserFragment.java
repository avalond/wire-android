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
import com.waz.api.User;

public class SingleImageUserFragment extends SingleImageFragment {

    public static final String TAG = SingleImageUserFragment.class.getName();
    private static final String ARG_USER = "ARG_USER";

    private User user;

    public static SingleImageUserFragment newInstance(User user) {
        SingleImageUserFragment fragment = new SingleImageUserFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().get(ARG_USER);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        user = null;
        super.onDestroyView();
    }

    @Override
    protected ImageAsset getImage() {
        return user.getPicture();
    }

    @Override
    protected ImageView.ScaleType getScaleType() {
        return ImageView.ScaleType.CENTER_CROP;
    }
}
