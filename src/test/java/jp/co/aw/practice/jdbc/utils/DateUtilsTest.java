package jp.co.aw.practice.jdbc.utils;

import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.assertTimestamp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.aw.practice.jdbc.ApplicationException;

import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void now_通常() {
        Timestamp ts = DateUtils.now();
        assertThat(ts, is(notNullValue()));
    }

    @Test
    public void ts_通常() {
        {
            Date d = new Date();
            assertTimestamp(DateUtils.ts(d), d);
        }
        {
            Timestamp ts = new Timestamp(new Date().getTime());
            assertTimestamp(DateUtils.ts(ts), ts);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_引数がnull() {
        DateUtils.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_引数が空() {
        DateUtils.parse("");
    }

    @Test(expected = ApplicationException.class)
    public void parse_フォーマット不正() {
        DateUtils.parse("xxx");
    }

    @Test
    public void parse_通常() throws ParseException {
        Timestamp ret = DateUtils.parse("2015/01/02 11:22:33");
        assertTimestamp(ret, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2015/01/02 11:22:33"));
    }

    @Test
    public void format_通常() throws ParseException {
        Date d = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2015/01/02 11:22:33");
        String ret = DateUtils.format(d);
        assertThat(ret, is("2015/01/02 11:22:33"));
    }

    @Test(expected = NullPointerException.class)
    public void format_引数がnull() {
        DateUtils.format(null);
    }
}
