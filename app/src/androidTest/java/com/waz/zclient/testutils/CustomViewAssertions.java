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

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import android.view.View;
import junit.framework.AssertionFailedError;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class CustomViewAssertions {

    public static ViewAssertion isGone() {
        return matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE));
    }

    public static ViewAssertion isInvisible() {
        return matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE));
    }

    public static ViewAssertion isVisible() {
        return matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE));
    }

    public static ViewAssertion hasText(String text) {
        return matches(withText(text));
    }

    public static ViewAssertion containsText(String text) {
        return matches(withText(containsString(text)));
    }

    public static ViewAssertion doesNotContainText(String text) {
        return matches(withText(not(containsString(text))));
    }

    public static ViewAssertion isNull() {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (view != null) {
                    throw new AssertionFailedError("View is not null");
                }
            }
        };
    }
}
