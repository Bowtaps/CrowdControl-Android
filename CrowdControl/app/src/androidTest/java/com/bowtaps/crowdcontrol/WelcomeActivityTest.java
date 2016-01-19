package com.bowtaps.crowdcontrol;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;
import com.parse.ParseUser;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

public class WelcomeActivityTest {
    @Before
    public void setUp() throws Exception {
//        ParseUser parseUser = new ParseUser();
    }

    @Test
    public void getUserFromLocalDatastore() {
        //TODO: retrieve local user from previous session,
        //assert that it is not null
        //OR that if it is null, it is intended to be null...
        ParseUser parseUser = ParseUser.getCurrentUser();
        assertEquals(CrowdControlApplication.getInstance().getModelManager().getCurrentUser(), parseUser);
    }

    @After
    public void tearDown() throws Exception {

    }
}