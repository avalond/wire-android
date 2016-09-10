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
package com.waz.zclient.controllers.navigation;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.waz.zclient.pages.main.calling.enums.VoiceBarAppearance;

public interface INavigationController extends ViewPager.OnPageChangeListener {

    VoiceBarAppearance getVoiceBarAppearanceConversationList();

    VoiceBarAppearance getVoiceBarAppearanceMessageStream();

    void setConversationListState(VoiceBarAppearance voiceBarAppearance);

    void setMessageStreamState(VoiceBarAppearance voiceBarAppearance);

    void addScreenPositionObserver(ScreenPositionObserver screenPositionObserver);

    void removeScreenPositionObserver(ScreenPositionObserver screenPositionObserver);

    void addPagerControllerObserver(PagerControllerObserver pagerControllerObserver);

    void removePagerControllerObserver(PagerControllerObserver pagerControllerObserver);

    void addNavigationControllerObserver(NavigationControllerObserver navigationControllerObserver);

    void removeNavigationControllerObserver(NavigationControllerObserver navigationControllerObserver);

    void setVisiblePage(Page page, String sender);

    void setPagerPosition(int position);

    int getPagerPosition();

    void resetPagerPositionToDefault();

    void setLeftPage(Page leftPage, String sender);

    void setRightPage(Page leftPage, String sender);

    Page getCurrentPage();

    Page getCurrentLeftPage();

    Page getCurrentRightPage();

    void onActivityCreated(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    void setScreenOffsetX(int x);

    void setScreenOffsetY(int y);

    int getScreenOffsetX();

    int getScreenOffsetY();

    void setScreenOffsetYFactor(float factorY);

    int getMaxScreenOffsetY();

    void setPagerEnabled(boolean enabled);

    void setPagerSettingForPage(Page page);

    boolean isPagerEnabled();

    void setIsLandscape(boolean isLandscape);

    /**
     * This will return true if the app is resumed in the background.
     * After onResume() it will return false
     */
    boolean isActivityResuming();

    void markActivityPaused();

    void markActivityResumed();

    void tearDown();
}
