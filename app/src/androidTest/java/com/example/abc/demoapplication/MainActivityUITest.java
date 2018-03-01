package com.example.abc.demoapplication;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.text.TextUtils;


import com.example.abc.demoapplication.model.NewsBean;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.core.Is.is;

/**
 * Created by abc on 2018/2/28
 */
public class MainActivityUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testNoData() {
        //id no_data TextView showed means no data/error
        onView(withId(R.id.tv_no_data)).check(matches(isDisplayed()));
    }

    @Test
    public void testAllData() {
        onData(allOf(is(instanceOf(NewsBean.class)), isLastExit("Language"))).inAdapterView(withId(R.id.list_view));
    }

    public static Matcher<Object> isLastExit(final String title) {
        return new BoundedMatcher<Object, NewsBean>(NewsBean.class) {

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(NewsBean item) {
                if (item != null
                        && !TextUtils.isEmpty(item.getTitle())
                        && item.getTitle().equals(title))
                    return true;
                return false;
            }
        };
    }

}