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
package com.waz.zclient.testutils;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import com.waz.zclient.R;
import com.waz.zclient.TestActivity;
import com.waz.zclient.mock.MockControllerFactory;
import com.waz.zclient.mock.MockStoreFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static junit.framework.Assert.assertTrue;

public class FragmentTest<A extends TestActivity> {

    public static final int CATCH_DEBUG_DELAY = 3000;
    public static final int WAIT_FOR_TRANSACTION_COMMIT_DELAY = 100;
    @Rule
    public ActivityTestRule<A> activityTestRule;

    protected Instrumentation instrumentation;

    protected A activity;
    private Class<A> activityType;

    public FragmentTest(Class<A> activityType) {
        this.activityType = activityType;
        activityTestRule = new ActivityTestRule<>(activityType, false, false);
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }

    @Before
    public void setup() throws InterruptedException {
        //Thread.sleep(CATCH_DEBUG_DELAY); // very useful for being able to catch with the debugger :)
        instrumentation = InstrumentationRegistry.getInstrumentation();
        Intents.init();
        activityTestRule.launchActivity(new Intent(instrumentation.getContext(), activityType));

        activity = activityTestRule.getActivity();
        activity.setMockStoreFactory(new MockStoreFactory());
        activity.setMockControllerFactory(new MockControllerFactory());
    }

    protected void attachFragment(Fragment fragment, String TAG) {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.test_content, fragment, TAG)
                .commit();

        try {
            Thread.sleep(WAIT_FOR_TRANSACTION_COMMIT_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue("Fragment didn't attach", fragment.isAdded());
    }

    @After
    public void tearDown() {
        Intents.release();
    }
}
