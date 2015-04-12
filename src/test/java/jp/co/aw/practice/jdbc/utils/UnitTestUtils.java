package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Strings;

public class UnitTestUtils {
    public static void assertTimestamp(Date ts, String str) throws ParseException {
        if (Strings.isNullOrEmpty(str)) {
            assertThat(ts, is(nullValue()));
            return;
        }
        assertThat(ts, is(notNullValue()));
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date expected = df.parse(str);
        assertThat(String.format(String.format("Expected [%s] but was [%s]", expected, ts)), ts.getTime(), is(expected.getTime()));
    }

    public static void assertTimestamp(Date ts, Date ts2) {
        if (ts2 == null) {
            assertThat(ts, is(nullValue()));
        }
        assertThat(ts, is(notNullValue()));
        assertThat(String.format(String.format("Expected [%s] but was [%s]", ts2, ts)), ts.getTime(), is(ts2.getTime()));
    }
}
