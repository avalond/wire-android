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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.localytics.android.Localytics;
import com.waz.api.Self;
import com.waz.api.TrackingData;
import com.waz.api.UpdateListener;
import com.waz.api.ZMessagingApi;
import com.waz.zclient.BuildConfig;
import com.waz.zclient.ZApplication;
import com.waz.zclient.controllers.tracking.events.launch.AppLaunch;
import com.waz.zclient.controllers.tracking.events.peoplepicker.PeoplePickerClosedByUser;
import com.waz.zclient.controllers.tracking.events.peoplepicker.PeoplePickerResultsUsed;
import com.waz.zclient.controllers.tracking.events.session.Session;
import com.waz.zclient.controllers.tracking.screens.ApplicationScreen;
import com.waz.zclient.controllers.tracking.screens.RegistrationScreen;
import com.waz.zclient.controllers.userpreferences.UserPreferencesController;
import com.waz.zclient.core.controllers.tracking.attributes.Attribute;
import com.waz.zclient.core.controllers.tracking.attributes.RangedAttribute;
import com.waz.zclient.core.controllers.tracking.events.AVSMetricEvent;
import com.waz.zclient.core.controllers.tracking.events.Event;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.ExceptionHandler;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrackingController implements ITrackingController {

    private static final long PAUSE_DURATION_THRESHOLD_MS = 15 * 1000;
    private static final String SAVED_STATE_SENT_TAGS = "SAVED_STATE_SENT_TAGS";
    private static final String QA_LOG_TAG = "TrackingController";

    protected TrackingData trackingData;
    protected List<Event> eventQueue;
    private SessionEventAggregator sessionEventAggregator;
    private Set<String> sentEvents;
    private Context context;
    private Self self;
    private SharedPreferences sentEventPreferences;
    private boolean appLaunchedTracked;
    private ApplicationScreen applicationScreen;

    public TrackingController() {
        this.sentEvents = new HashSet<>();
        this.eventQueue = new ArrayList<>();
    }

    @Override
    public void setActivity(Activity activity) {
        if (context != null) {
            return;
        }
        this.sessionEventAggregator = new SessionEventAggregator(activity.getApplicationContext());
        ZMessagingApi api = ZApplication.from(activity).getStoreFactory().getZMessagingApiStore().getApi();
        this.self = api.getSelf();
        this.trackingData = api.getTrackingData();
        this.context = activity.getApplicationContext();
        sentEventPreferences = context.getSharedPreferences("TRACKING_SENT_EVENTS", Context.MODE_PRIVATE);
        trackingData.addUpdateListener(trackingDataUpdateListener);

    }

    private void setCustomUserId() {
        String trackingId = null;

        if (self != null && self.getUser() != null) {
            trackingId = self.getTrackingId();
        }

        Localytics.setCustomerId(trackingId);
    }

    @Override
    public void tagEvent(Event event) {
        if (BuildConfig.SHOW_DEVELOPER_OPTIONS) {
            // Log tracking for candidate builds
            Timber.tag(QA_LOG_TAG).e("Tag event=[name='%s',\nattributes='%s',\nrangedAttributes='%s']",
                                  event.getName(),
                                  event.getAttributes().toString(),
                                  event.getRangedAttributes().toString());
        }

        if (event.onlySendOnce()) {
            if (isEventSent(event)) {
                Timber.i("Ignoring event %s, already been sent.", event.getName());
                return;
            }
            setEventSent(event);
        }

        if (trackingData == null ||
            (!trackingData.isInitialized() &&
             event.mustWaitForTrackingData())) {
            Timber.i("Tried to tag event %s but trackingData not yet initialized.", event.getName());
            eventQueue.add(event);
            return;
        }
        event.addSyncEngineTrackingData(trackingData);
        HashMap<String, String> eventAttributes = new HashMap<>();
        for (RangedAttribute attribute : event.getRangedAttributes().keySet()) {
            int eventCount = event.getRangedAttributes().get(attribute);
            eventAttributes.put(attribute.name, createRangedAttribute(eventCount, attribute.rangeSteps));
            eventAttributes.put(attribute.actualValueName, Integer.toString(eventCount));
        }

        for (Attribute attribute : event.getAttributes().keySet()) {
            eventAttributes.put(attribute.name, event.getAttributes().get(attribute));
        }

        // Needs to be done in every tag, because we have otherwise no user after login
        setCustomUserId();
        List<String> customDimensions = getCustomDimensions();
        for (int i = 0; i < customDimensions.size(); i++) {
            Localytics.setCustomDimension(i, customDimensions.get(i));
        }
        Localytics.tagEvent(event.getName(), eventAttributes);
    }

    @Override
    public void tagAVSMetricEvent(AVSMetricEvent event) {
        if (event.onlySendOnce()) {
            if (isEventSent(event)) {
                Timber.i("Ignoring event %s, already been sent.", event.getName());
                return;
            }
            setEventSent(event);
        }

        event.addSyncEngineTrackingData(trackingData);
        HashMap<String, String> eventAttributes = new HashMap<>();
        for (String attribute : event.getAttributes().keySet()) {
            eventAttributes.put(attribute, event.getAttributes().get(attribute));
        }

        // Needs to be done in every tag, because we have otherwise no user after login
        setCustomUserId();
        List<String> customDimensions = getCustomDimensions();
        for (int i = 0; i < customDimensions.size(); i++) {
            Localytics.setCustomDimension(i, customDimensions.get(i));
        }
        Localytics.tagEvent(event.getName(), eventAttributes);
    }

    private boolean isEventSent(Event event) {
        return sentEventPreferences.getBoolean(event.getName(), false);
    }

    private boolean isEventSent(AVSMetricEvent event) {
        return sentEventPreferences.getBoolean(event.getName(), false);
    }

    private void setEventSent(Event event) {
        sentEventPreferences.edit().putBoolean(event.getName(), true).apply();
    }

    private void setEventSent(AVSMetricEvent event) {
        sentEventPreferences.edit().putBoolean(event.getName(), true).apply();
    }

    protected List<String> getCustomDimensions() {
        final List<String> customDimensions = new ArrayList<>();

        // has interacted with bot
        customDimensions.add(String.valueOf(trackingData.hasInteractedWithBot()));

        // number of auto-connected contacts
        customDimensions.add(createRangedAttribute(trackingData.getAutoConnectedContactCount(),
                                                   RangedAttribute.NUMBER_OF_CONTACTS.rangeSteps));

        // number of contacts
        customDimensions.add(createRangedAttribute(trackingData.getNotBlockedContactCount(),
                                                   RangedAttribute.NUMBER_OF_CONTACTS.rangeSteps));

        // number of groups
        customDimensions.add(createRangedAttribute(trackingData.getGroupConversationCount(),
                                                   RangedAttribute.NUMBER_OF_GROUP_CONVERSATIONS.rangeSteps));

        // number of voice calls
        customDimensions.add(createRangedAttribute(trackingData.getVoiceCallCount(),
                                                   RangedAttribute.NUMBER_OF_VOICE_CALLS.rangeSteps));


        // number of video calls
        customDimensions.add(createRangedAttribute(trackingData.getVideoCallCount(),
                                                   RangedAttribute.NUMBER_OF_VIDEO_CALLS.rangeSteps));


        // number of text messages
        customDimensions.add(createRangedAttribute(trackingData.getSentTextMessageCount(),
                                                   RangedAttribute.TEXT_MESSAGES_SENT.rangeSteps));

        // number of images
        customDimensions.add(createRangedAttribute(trackingData.getSentImagesCount(),
                                                   RangedAttribute.IMAGES_SENT.rangeSteps));

        // AB testing group
        SharedPreferences preferences = context.getSharedPreferences(UserPreferencesController.USER_PREFS_TAG,
                                                                     Context.MODE_PRIVATE);
        customDimensions.add(Integer.toString(preferences.getInt(UserPreferencesController.USER_PERFS_AB_TESTING_GROUP,
                                                                 0)));

        // network info
        customDimensions.add(getNetworkClass());


        return customDimensions;
    }

    @SuppressLint("DefaultLocale")
    private String getHexString(int value) {
        String hex = Integer.toHexString(value);
        if (hex.length() == 0) {
            hex = "00";
        } else if (hex.length() == 1) {
            hex = String.format("0%s", hex);
        }

        return hex.toUpperCase();
    }

    private String getNetworkClass() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return "unknown";
        }

        int networkInfoType = networkInfo.getType();
        if (networkInfoType == ConnectivityManager.TYPE_WIFI) {
            return "wifi";
        }
        switch (networkInfo.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "unknown";
        }
    }

    ////////////////////////////////////////////
    //
    // App opened/closed
    //
    ////////////////////////////////////////////

    @Override
    public void appLaunched(Intent intent) {
        if (!appLaunchedTracked) {
            AppLaunch event = new AppLaunch(intent);
            tagEvent(event);
            appLaunchedTracked = true;
        }
    }

    @Override
    public void appResumed() {
        pushAggregateSessionData();
    }

    @Override
    public void appPaused() {
        sessionEventAggregator.markPauseTime();
    }

    @Override
    public void tearDown() {
        self = null;
        if (trackingData != null) {
            trackingData.removeUpdateListener(trackingDataUpdateListener);
            trackingData = null;
        }
        trackingDataUpdateListener = null;
        context = null;
        sentEventPreferences = null;
    }

    @Override
    public void loadFromSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        String[] savedSentTags = savedInstanceState.getStringArray(SAVED_STATE_SENT_TAGS);
        if (savedSentTags != null) {
            sentEvents = new HashSet<>(Arrays.asList(savedSentTags));
        }
    }

    @Override
    public void saveToSavedInstance(Bundle outState) {
        outState.putStringArray(SAVED_STATE_SENT_TAGS, sentEvents.toArray(new String[sentEvents.size()]));
    }

    ////////////////////////////////////////////
    //
    // Session tracking
    //
    ////////////////////////////////////////////

    @Override
    public void updateSessionAggregates(RangedAttribute attribute, String... params) {
        if (sessionEventAggregator == null) {
            return;
        }
        try {
            sessionEventAggregator.incrementEventCount(attribute, params);
        } catch (Exception e) {
            ExceptionHandler.saveException(e, new CrashManagerListener() {
                @Override
                public String getDescription() {
                    return "try/catch logged";
                }
            });
        }
    }

    @Override
    public void markAsFirstSession() {
        sessionEventAggregator.markAsFirstSession();
    }

    @Override
    public void searchedForPeople() {
        if (sessionEventAggregator == null) {
            return;
        }
        sessionEventAggregator.searchedForPeople();
    }

    private void pushAggregateSessionData() {
        if (sessionEventAggregator.getElapsedTimeSincePause() <= PAUSE_DURATION_THRESHOLD_MS) {
            return;
        }

        Session sessionEvent = new Session(sessionEventAggregator.getEventCount(RangedAttribute.CONNECT_REQUESTS_SENT),
                                           sessionEventAggregator.getEventCount(RangedAttribute.CONNECT_REQUESTS_ACCEPTED),
                                           sessionEventAggregator.getEventCount(RangedAttribute.VOICE_CALLS_INITIATED),
                                           sessionEventAggregator.getEventCount(RangedAttribute.INCOMING_CALLS_ACCEPTED),
                                           sessionEventAggregator.getEventCount(RangedAttribute.INCOMING_CALLS_SILENCED),
                                           sessionEventAggregator.getEventCount(RangedAttribute.GROUP_CONVERSATIONS_STARTED),
                                           sessionEventAggregator.getEventCount(RangedAttribute.USERS_ADDED_TO_CONVERSATIONS),
                                           sessionEventAggregator.getEventCount(RangedAttribute.TEXT_MESSAGES_SENT),
                                           sessionEventAggregator.getEventCount(RangedAttribute.YOUTUBE_LINKS_SENT),
                                           sessionEventAggregator.getEventCount(RangedAttribute.SOUNDCLOUD_LINKS_SENT),
                                           sessionEventAggregator.getEventCount(RangedAttribute.PINGS_SENT),
                                           sessionEventAggregator.getEventCount(RangedAttribute.IMAGES_SENT),
                                           sessionEventAggregator.getEventCount(RangedAttribute.IMAGE_CONTENT_CLICKS),
                                           sessionEventAggregator.getEventCount(RangedAttribute.SOUNDCLOUD_CONTENT_CLICKS),
                                           sessionEventAggregator.getEventCount(RangedAttribute.YOUTUBE_CONTENT_CLICKS),
                                           sessionEventAggregator.getEventCount(RangedAttribute.CONVERSATION_RENAMES),
                                           sessionEventAggregator.getEventCount(RangedAttribute.SESSION_DURATION),
                                           sessionEventAggregator.getEventCount(RangedAttribute.OPENED_SEARCH),
                                           sessionEventAggregator.isFirstSession(),
                                           sessionEventAggregator.hasSearchedForPeople()
        );

        tagEvent(sessionEvent);

        sessionEventAggregator.restartSession();
        Localytics.upload();
    }

    // //////////////////////////////////////////////////////
    // Registration
    // //////////////////////////////////////////////////////

    @Override
    public void onRegistrationScreen(RegistrationScreen screen) {
        if (screen == null) {
            return;
        }
        tagRegistrationScreen(screen.toString());
    }

    private void tagRegistrationScreen(String screen) {
        if (sentEvents.contains(screen)) {
            return;
        }
        sentEvents.add(screen);
        Localytics.tagScreen(screen);
    }

    // //////////////////////////////////////////////////////
    // Profile
    // //////////////////////////////////////////////////////

    @Override
    public void onApplicationScreen(ApplicationScreen screen) {
        Localytics.tagScreen(screen.toString());
        applicationScreen = screen;
    }

    @Override
    public ApplicationScreen getApplicationScreen() {
        return applicationScreen;
    }

    ////////////////////////////////////////////
    //
    // People Picker
    //
    ////////////////////////////////////////////

    @Override
    public void onPeoplePickerResultsUsed(int numberOfContacts, PeoplePickerResultsUsed.Usage usage) {
        PeoplePickerResultsUsed event = new PeoplePickerResultsUsed(numberOfContacts, usage);
        tagEvent(event);
    }

    @Override
    public void onPeoplePickerClosedByUser(boolean searchBoxHasOnlyStringContent, boolean cancelledByUser) {
        if (searchBoxHasOnlyStringContent && cancelledByUser) {
            tagEvent(new PeoplePickerClosedByUser());
        }
    }

    ////////////////////////////////////////////
    //
    // Tracking data update listener
    //
    ////////////////////////////////////////////

    protected UpdateListener trackingDataUpdateListener = new UpdateListener() {
        @Override
        public void updated() {
            List<Event> retryList = new ArrayList<>(eventQueue);
            eventQueue.clear();
            Timber.i("trackingData initialized, tagging %d event(s):", retryList.size());
            for (Event event : retryList) {
                Timber.i(event.getName());
                tagEvent(event);
            }
        }
    };

    /**
     * This part (the method createRangedAttribute) of the Wire software uses source code from the beats2 project.
     * (https://code.google.com/p/beats2/source/browse/trunk/beats/src/com/localytics/android/LocalyticsSession.java#810)
     *
     * Copyright (c) 2009, Char Software, Inc. d/b/a Localytics
     * All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     *  - Redistributions of source code must retain the above copyright
     *    notice, this list of conditions and the following disclaimer.
     *  - Neither the name of Char Software, Inc., Localytics nor the names of its
     *    contributors may be used to endorse or promote products derived from this
     *    software without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY CHAR SOFTWARE, INC. D/B/A LOCALYTICS ''AS IS'' AND
     * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
     * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
     * DISCLAIMED. IN NO EVENT SHALL CHAR SOFTWARE, INC. D/B/A LOCALYTICS BE LIABLE
     * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
     * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
     * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
     * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
     * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
     * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     *
     * Sorts an int value into a predefined, pre-sorted set of intervals, returning a string representing the new expected value.
     * The array must be sorted in ascending order, with the first element representing the inclusive lower bound and the last
     * element representing the exclusive upper bound. For instance, the array [0,1,3,10] will provide the following buckets: less
     * than 0, 0, 1-2, 3-9, 10 or greater.
     *
     * @param actualValue The int value to be bucketed.
     * @param steps       The sorted int array representing the bucketing intervals.
     * @return String representation of {@code actualValue} that has been bucketed into the range provided by {@code steps}.
     * @throws IllegalArgumentException if {@code steps} is null.
     * @throws IllegalArgumentException if {@code steps} has length 0.
     */
    public static String createRangedAttribute(final int actualValue, final int[] steps) {
        if (null == steps) {
            throw new IllegalArgumentException("steps cannot be null"); //$NON-NLS-1$
        }

        if (steps.length == 0) {
            throw new IllegalArgumentException("steps length must be greater than 0"); //$NON-NLS-1$
        }

        String bucket;

        // if less than smallest value
        if (actualValue < steps[0]) {
            bucket = "less than " + steps[0];
        } else if (actualValue >= steps[steps.length - 1]) { // if greater than largest value
            bucket = steps[steps.length - 1] + " and above";
        } else {
            // binarySearch returns the index of the value, or (-(insertion point) - 1) if not found
            int bucketIndex = Arrays.binarySearch(steps, actualValue);
            if (bucketIndex < 0) {
                // if the index wasn't found, then we want the value before the insertion point as the lower end
                // the special case where the insertion point is 0 is covered above, so we don't have to worry about it here
                bucketIndex = (-bucketIndex) - 2;
            }
            if (steps[bucketIndex] == (steps[bucketIndex + 1] - 1)) {
                bucket = Integer.toString(steps[bucketIndex]);
            } else {
                bucket = steps[bucketIndex] + "-" + (steps[bucketIndex + 1] - 1); //$NON-NLS-1$
            }
        }
        return bucket;
    }
}
