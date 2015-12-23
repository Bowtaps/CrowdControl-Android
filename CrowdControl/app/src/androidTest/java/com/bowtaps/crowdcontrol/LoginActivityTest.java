package com.bowtaps.crowdcontrol;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import com.bowtaps.crowdcontrol.LoginActivity;

import junit.framework.TestCase;

/**
 * Created by Joe on 12/22/2015.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity loginActivity;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loginActivity = getActivity();
    }

    @SmallTest
    public void textTextViewNotFull() {
        TextView textView = (TextView) loginActivity.findViewById(R.id.textView);
        assertNotNull(textView);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
