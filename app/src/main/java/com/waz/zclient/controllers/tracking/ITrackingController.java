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
package com.waz.zclient.controllers.tracking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.waz.zclient.controllers.tracking.events.peoplepicker.PeoplePickerResultsUsed;
import com.waz.zclient.controllers.tracking.screens.ApplicationScreen;
import com.waz.zclient.controllers.tracking.screens.RegistrationScreen;
import com.waz.zclient.core.controllers.tracking.attributes.RangedAttribute;
import com.waz.zclient.core.controllers.tracking.events.AVSMetricEvent;
import com.waz.zclient.core.controllers.tracking.events.Event;

public interface ITrackingController {

    void appLaunched(Intent intent);
    void appResumed();
    void appPaused();
    void tearDown();
    void tagEvent(Event event);
    void tagAVSMetricEvent(AVSMetricEvent event);

    void loadFromSavedInstance(Bundle savedInstanceState);
    void saveToSavedInstance(Bundle outState);

    // Session data
    void updateSessionAggregates(RangedAttribute attribute, String... params);
    void markAsFirstSession();
    void searchedForPeople();

    // Registration
    void onRegistrationScreen(RegistrationScreen screen);

    void onApplicationScreen(ApplicationScreen screen);
    ApplicationScreen getApplicationScreen();

    // People picker
    void onPeoplePickerResultsUsed(int numberOfContacts, PeoplePickerResultsUsed.Usage usage);
    void onPeoplePickerClosedByUser(boolean searchBoxHasContent, boolean cancelledByUser);

    void setActivity(Activity activity);
}
