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
package com.waz.zclient.controllers.stubs;

import com.waz.api.IConversation;
import com.waz.zclient.controllers.navigation.Page;
import com.waz.zclient.controllers.onboarding.IOnboardingController;
import com.waz.zclient.controllers.onboarding.OnboardingControllerObserver;
import com.waz.zclient.pages.main.onboarding.OnBoardingHintType;
import java.lang.Override;

public class StubOnboardingController implements IOnboardingController {
  @Override
  public void setCurrentHintType(OnBoardingHintType hintType) {
    ;
  }

  @Override
  public void incrementPeoplePickerShowCount() {
    ;
  }

  @Override
  public void hideOnboardingHint(OnBoardingHintType requestedType) {
    ;
  }

  @Override
  public void hideConversationListHint() {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public OnBoardingHintType getCurrentVisibleHintType() {
    return null;
  }

  @Override
  public void incrementParticipantsShowCount() {
    ;
  }

  @Override
  public void addOnboardingControllerObserver(OnboardingControllerObserver onboardingControllerObserver) {
    ;
  }

  @Override
  public void incrementSwipeToConversationListCount(Page currentRightPage) {
    ;
  }

  @Override
  public OnBoardingHintType getCurrentOnBoardingHint(Page currentPage, IConversation currentConversation, boolean conversationHasDraft) {
    return null;
  }

  @Override
  public void removeOnboardingControllerObserver(OnboardingControllerObserver onboardingControllerObserver) {
    ;
  }

  @Override
  public boolean shouldShowConversationListHint() {
    return false;
  }
}
