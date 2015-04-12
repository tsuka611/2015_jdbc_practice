package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

public class UnitTestUtils {
    public static void assertTimestamp(Date ts, Date ts2) {
        if (ts2 == null) {
            assertThat(ts, is(nullValue()));
            return;
        }
        assertThat(ts, is(notNullValue()));
        assertThat(String.format(String.format("Expected [%s] but was [%s]", ts2, ts)), ts.getTime(), is(ts2.getTime()));
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
