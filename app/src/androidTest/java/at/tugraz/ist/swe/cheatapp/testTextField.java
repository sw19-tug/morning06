package at.tugraz.ist.swe.cheatapp;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class testTextField {
    @Test
    public void testFieldVisible() {
        onView(withId(R.id.textEntry)).check(matches(isDisplayed()));
    }
}
