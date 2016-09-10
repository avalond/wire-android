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
package com.waz.zclient.pages.main.onboarding;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.waz.api.IConversation;
import com.waz.zclient.R;
import com.waz.zclient.controllers.accentcolor.AccentColorObserver;
import com.waz.zclient.core.stores.conversation.OnConversationLoadedListener;
import com.waz.zclient.pages.BaseFragment;

public class OnBoardingHintFragment extends BaseFragment<OnBoardingHintFragment.Container> implements AccentColorObserver,
                                                                                                      OnConversationLoadedListener {

    public static final String TAG = OnBoardingHintFragment.class.getName();
    public static final String ARGUMENT_HINT_TYPE = "ARGUMENT_HINT_TYPE";


    private View hintContainer;

    public static OnBoardingHintFragment newInstance(OnBoardingHintType hintType) {
        OnBoardingHintFragment newFragment = new OnBoardingHintFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HINT_TYPE, hintType.toString());
        newFragment.setArguments(args);

        return newFragment;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Lifecycle
    //
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_dialog_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getControllerFactory().getAccentColorController().addAccentColorObserver(this);
    }

    @Override
    public void onStop() {
        getControllerFactory().getAccentColorController().removeAccentColorObserver(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        hintContainer = null;
        super.onDestroyView();
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //  AccentColorObserver
    //
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onAccentColorHasChanged(Object sender, int color) {
        if (hintContainer == null) {
            return;
        }
        GradientDrawable circleDrawable = (GradientDrawable) hintContainer.getBackground();
        circleDrawable.setColor(color);
    }

    public OnBoardingHintType getOnBoardingHintType() {
        return OnBoardingHintType.valueOf(getArguments().getString(ARGUMENT_HINT_TYPE));
    }

    @Override
    public void onConversationLoaded(IConversation conversation) {

    }

    public interface Container {
        void dismissOnBoardingHint(OnBoardingHintType requestedType);
    }
}
