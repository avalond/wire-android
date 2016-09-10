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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.waz.zclient.controllers.tracking.ITrackingController;
import com.waz.zclient.controllers.tracking.events.peoplepicker.PeoplePickerResultsUsed;
import com.waz.zclient.controllers.tracking.screens.ApplicationScreen;
import com.waz.zclient.controllers.tracking.screens.RegistrationScreen;
import com.waz.zclient.core.controllers.tracking.attributes.RangedAttribute;
import com.waz.zclient.core.controllers.tracking.events.AVSMetricEvent;
import com.waz.zclient.core.controllers.tracking.events.Event;
import java.lang.Override;
import java.lang.String;

public class StubTrackingController implements ITrackingController {
  @Override
  public ApplicationScreen getApplicationScreen() {
    return null;
  }

  @Override
  public void markAsFirstSession() {
    ;
  }

  @Override
  public void saveToSavedInstance(Bundle outState) {
    ;
  }

  @Override
  public void onApplicationScreen(ApplicationScreen screen) {
    ;
  }

  @Override
  public void onPeoplePickerResultsUsed(int numberOfContacts, PeoplePickerResultsUsed.Usage usage) {
    ;
  }

  @Override
  public void searchedForPeople() {
    ;
  }

  @Override
  public void onPeoplePickerClosedByUser(boolean searchBoxHasContent, boolean cancelledByUser) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void appPaused() {
    ;
  }

  @Override
  public void tagAVSMetricEvent(AVSMetricEvent event) {
    ;
  }

  @Override
  public void onRegistrationScreen(RegistrationScreen screen) {
    ;
  }

  @Override
  public void setActivity(Activity activity) {
    ;
  }

  @Override
  public void loadFromSavedInstance(Bundle savedInstanceState) {
    ;
  }

  @Override
  public void updateSessionAggregates(RangedAttribute attribute, String[] params) {
    ;
  }

  @Override
  public void appResumed() {
    ;
  }

  @Override
  public void tagEvent(Event event) {
    ;
  }

  @Override
  public void appLaunched(Intent intent) {
    ;
  }
}
