package com.bowtaps.crowdcontrol;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Joe on 12/22/2015.
 */
@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {

    @Rule public final ActivityTestRule<WelcomeActivity> welcome = new ActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void shouldBeAbleToLaunchMainScreen() {
        onView(withText("Crowd Control")).check(ViewAssertions.matches(isDisplayed()));
    }

}
